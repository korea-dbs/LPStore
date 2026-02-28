package com.example.tester

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.concurrent.atomic.AtomicInteger

data class ImageItem(val id: Long, val uri: android.net.Uri, val displayName: String)

data class PerformanceMetrics(
    var dbQueryTime: Long = 0,
    var imageLoadTime: Long = 0,
    var totalTime: Long = 0,
    var imageCount: Int = 0,
    var imageLoadStartTime: Long = 0,
    var firstImageLoadTime: Long = 0,
    var renderComplete100Time: Long = 0  // rendering 100 samples for UI rendering
)

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var metricsTextView: TextView
    private val images = mutableListOf<ImageItem>()
    private val metrics = PerformanceMetrics()

    private var metricsCollectionActive = true

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val TAG = "GalleryAnalyzer"
        private const val METRICS_STOP_THRESHOLD = 100  // halt test when rendering 100 is done
        private const val GRID_COLUMNS = 10  // 10 * 10 grid for exhibition
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        metricsTextView = findViewById(R.id.metricsTextView)

        // set the grid with 10 * 10
        recyclerView.layoutManager = GridLayoutManager(this, GRID_COLUMNS)

        if (checkPermissions()) {
            loadImages()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImages()
            } else {
                metricsTextView.text = "Permission is required, please allow access to the storage."
            }
        }
    }

    private fun loadImages() {
        val totalStartTime = System.currentTimeMillis()

        Log.d(TAG, "load Images started")
        metricsTextView.text = "Searching for the images..."

        CoroutineScope(Dispatchers.IO).launch {
            val dbStartTime = System.currentTimeMillis()
            Log.d(TAG, "DB Query Execution Started...")
            val imageList = queryMediaStore()
            val dbEndTime = System.currentTimeMillis()
            metrics.dbQueryTime = dbEndTime - dbStartTime
            metrics.imageCount = imageList.size

            Log.d(TAG, "DB Query Execution Completed: ${imageList.size} Images Gathered, ${metrics.dbQueryTime}ms")

            if (imageList.isEmpty()) {
                withContext(Dispatchers.Main) {
                    metricsTextView.text = "⚠️ No Images in your Gallery.\n\nAdd pictures for the test."
                }
                return@launch
            }

            withContext(Dispatchers.Main) {
                images.clear()
                images.addAll(imageList)

                metricsTextView.text = "Loading Images number (${imageList.size})"

                metrics.imageLoadStartTime = System.currentTimeMillis()

                Log.d(TAG, "RecyclerView Adapter Settings Initiated")

                recyclerView.adapter = ImageAdapter(
                    images,
                    onFirstImageLoaded = { loadTime ->
                        if (metricsCollectionActive) {
                            metrics.firstImageLoadTime = loadTime
                            Log.d(TAG, "Loading First Image Completed in : ${loadTime}ms")
                        }
                        updateMetricsDisplay()
                    },
                    onImageLoaded = { count ->
                        // balt when all 100 images are properly loaded
                        if (count >= METRICS_STOP_THRESHOLD && metricsCollectionActive) {
                            metricsCollectionActive = false
                            metrics.renderComplete100Time = System.currentTimeMillis()
                            metrics.imageLoadTime = metrics.renderComplete100Time - metrics.imageLoadStartTime
                            metrics.totalTime = metrics.renderComplete100Time - totalStartTime

                            Log.d(TAG, "=== Rendering Completed ===")
                            Log.d(TAG, "Rendering Time: ${metrics.imageLoadTime}ms")
                            Log.d(TAG, "Total : ${metrics.totalTime}ms")
                            updateMetricsDisplay()
                        } else if (metricsCollectionActive) {
                            updateMetricsDisplay()
                        }
                    },
                    onAllImagesLoaded = { totalLoadTime ->
                        if (metricsCollectionActive) {
                            metrics.imageLoadTime = totalLoadTime
                            metrics.totalTime = System.currentTimeMillis() - totalStartTime
                            metricsCollectionActive = false

                            Log.d(TAG, "Image Loaded Completed At : ${totalLoadTime}ms")
                        }
                        updateMetricsDisplay()
                    }
                )

                Log.d(TAG, "RecyclerView Adapted Setting Completed")
            }
        }
    }

    private fun queryMediaStore(): List<ImageItem> {
        val imageList = mutableListOf<ImageItem>()

        val projectionList = mutableListOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.BUCKET_ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projectionList.add(MediaStore.Files.FileColumns.DURATION)
            projectionList.add(MediaStore.Images.Media.DATE_TAKEN)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            projectionList.add(MediaStore.Files.FileColumns.IS_FAVORITE)
        }

        val projection = projectionList.toTypedArray()

        //You can put your own query via selection
        /*val selection = buildString {
            append("(")
            append("${MediaStore.Files.FileColumns.SIZE} > 0")


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                append(" AND ${MediaStore.Files.FileColumns.IS_PENDING} = 0")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                append("AND ${MediaStore.Files.FileColumns.IS_TRASHED} = 0")
            }

            append(")")
        }*/

        val sortOrder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "(CASE WHEN ${MediaStore.Images.Media.DATE_TAKEN} > 0 " +
                    "THEN ${MediaStore.Images.Media.DATE_TAKEN} " +
                    "ELSE ${MediaStore.Files.FileColumns.DATE_ADDED} * 1000 END) DESC"
        } else {
            "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        }

        try {
            contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                //selection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                Log.d(TAG, "Query Result: ${cursor.count} Records Founded")

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val bucketNameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)

                var rowCount = 0
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val bucketName = if (bucketNameColumn >= 0) cursor.getString(bucketNameColumn) else "Unknown"

                    val uri = ContentUris.withAppendedId(
                        MediaStore.Files.getContentUri("external"),
                        id
                    )

                    imageList.add(ImageItem(id, uri, bucketName))
                    rowCount++
                }

                Log.d(TAG, " ${rowCount} Images Loaded")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Query Failed", e)
        }

        return imageList
    }

    private fun updateMetricsDisplay() {
        val df = DecimalFormat("#,###")
        val loadedCount = recyclerView.adapter?.let { adapter ->
            (adapter as? ImageAdapter)?.getLoadedCount() ?: 0
        } ?: 0

        val metricsStatus = if (metricsCollectionActive) "Testing..." else "Test Complete"

        val metricsText = """
            ━━━━━━ Performance Analysiss ━━━━━━
            
            📊 Total Number of Images: ${df.format(metrics.imageCount)}
            🔄 rendered : ${df.format(loadedCount)}/${df.format(metrics.imageCount)}
            📈 Metric Status: $metricsStatus
            
            ⏱️ Time consumption:
            
            1️⃣ SQLite DB Execution Time
               (MediaStore/external.db checked)
               → ${metrics.dbQueryTime}ms (${String.format("%.2f", metrics.dbQueryTime / 1000.0)} s)
            
            2️⃣ Image Rendering (First 100 images)
               ├─ First Image Rendering : ${metrics.firstImageLoadTime}ms
               ${if (!metricsCollectionActive) "├─ Completion Time: ${metrics.imageLoadTime}ms (${String.format("%.2f", metrics.imageLoadTime / 1000.0)}s)" else ""}
               ${if (!metricsCollectionActive && loadedCount > 0) "└─ Average: ${metrics.imageLoadTime / minOf(loadedCount, METRICS_STOP_THRESHOLD)}ms/pic" else ""}
               → ${if (metricsCollectionActive) "Testing..." else "Total ${metrics.imageLoadTime}ms"}
            
            3️⃣ Total Test Time
               → ${if (metricsCollectionActive) "Testing..." else "${metrics.totalTime}ms (${String.format("%.2f", metrics.totalTime / 1000.0)}s)"}
            
            ━━━━━━━━━━━━━━━━━━━━━━
            
            💡 Analysis:
            ${if (!metricsCollectionActive) {
            """- Query Execution: ${String.format("%.1f", metrics.dbQueryTime.toDouble() / metrics.totalTime * 100)}%
            - Rendering 100 Images: ${String.format("%.1f", metrics.imageLoadTime.toDouble() / metrics.totalTime * 100)}%
            - Others (overhead etc...): ${String.format("%.1f", (metrics.totalTime - metrics.dbQueryTime - metrics.imageLoadTime).toDouble() / metrics.totalTime * 100)}%
            - Throughput: ${String.format("%.1f", METRICS_STOP_THRESHOLD.toDouble() / (metrics.imageLoadTime / 1000.0))}장/초"""
        } else {
            "- Testing on progress"
        }}
        """.trimIndent()

        metricsTextView.text = metricsText

        if (!metricsCollectionActive) {
            Log.d(TAG, metricsText)
        }
    }
}

class ImageAdapter(
    private val images: List<ImageItem>,
    private val onFirstImageLoaded: (Long) -> Unit,
    private val onImageLoaded: (Int) -> Unit,
    private val onAllImagesLoaded: (Long) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val loadedCount = AtomicInteger(0)
    private var firstImageTime: Long = 0
    private val individualLoadTimes = mutableListOf<Long>()

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageItem = images[position]
        val startTime = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Thumbnail Settings (To fit in 10 * 10 grid)
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 4  // Set size freely to adjust setup environment.
                }

                val thumbnail = holder.itemView.context.contentResolver.openInputStream(imageItem.uri)?.use {
                    BitmapFactory.decodeStream(it, null, options)
                }

                val loadTime = System.currentTimeMillis() - startTime

                withContext(Dispatchers.Main) {
                    holder.imageView.setImageBitmap(thumbnail)

                    val currentCount = loadedCount.incrementAndGet()

                    synchronized(individualLoadTimes) {
                        individualLoadTimes.add(loadTime)
                    }

                    // Load First Images
                    if (currentCount == 1) {
                        firstImageTime = loadTime
                        onFirstImageLoaded(loadTime)
                    }

                    // Callback Sequence
                    onImageLoaded(currentCount)

                    // Load all images
                    if (currentCount == images.size) {
                        val totalLoadTime = synchronized(individualLoadTimes) {
                            individualLoadTimes.sum()
                        }
                        onAllImagesLoaded(totalLoadTime)

                        Log.d("ImageAdapter", "Loading Finished, Statistics:")
                        Log.d("ImageAdapter", "- Min: ${individualLoadTimes.minOrNull()}ms")
                        Log.d("ImageAdapter", "- Max: ${individualLoadTimes.maxOrNull()}ms")
                        Log.d("ImageAdapter", "- Avg: ${totalLoadTime / images.size}ms")
                    }
                }
            } catch (e: Exception) {
                Log.e("ImageAdapter", "Failed to Load images: ${imageItem.displayName}", e)

                val currentCount = loadedCount.incrementAndGet()

                withContext(Dispatchers.Main) {
                    onImageLoaded(currentCount)

                    if (currentCount == images.size) {
                        val totalLoadTime = synchronized(individualLoadTimes) {
                            individualLoadTimes.sum()
                        }
                        onAllImagesLoaded(totalLoadTime)
                    }
                }
            }
        }
    }

    override fun getItemCount() = images.size

    fun getLoadedCount(): Int = loadedCount.get()
}