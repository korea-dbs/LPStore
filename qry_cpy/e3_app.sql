SELECT _id, bucket_id, media_type, bucket_display_name, _data, date_added, mime_type, duration, width, height, is_favorite
FROM files
WHERE
	((_size > 0))
	AND
	(media_type  = '1')
	AND
	((is_pending=0 AND is_trashed=0))
	AND
	(volume_name = 'external_primary')
ORDER BY (CASE WHEN datetaken > 0 THEN datetaken ELSE date_added*1000 END) DESC;
;
