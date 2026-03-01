import pymysql
import sys

name="mysql_96col_ovfl_"+sys.argv[1]+"_text"

conn = pymysql.connect(
        unix_socket="/tmp/mysql.sock",
        user="root",
        password="1234"
        )

cursor = conn.cursor()

cursor.execute(f"CREATE DATABASE IF NOT EXISTS `{name}`;")

cursor.execute(f"USE `{name}`;")


'''
work smarter, not harder (lol)
'''
cursor.execute("""
 CREATE TABLE IF NOT EXISTS test (_id INTEGER,
     samp TEXT,
     _size INTEGER default 4096,
     format INTEGER default 3,
     parent INTEGER default 1,
     date_added INTEGER default 103498,
     date_modified INTEGER default 105583,
     mime_type TEXT default NULL,
     title TEXT default NULL,
     description TEXT,
     _display_name TEXT DEFAULT NULL,
     picasa_id TEXT DEFAULT NULL,
     orientation INTEGER default 0,
     latitude DOUBLE default 0.0,
     longitude DOUBLE default 0.0,
     datetaken INTEGER default 10220,
     mini_thumb_magic INTEGER,
     bucket_id TEXT default NULL,
     bucket_display_name TEXT default NULL,
     isprivate INTEGER default 0,
     title_key TEXT default NULL,
     artist_id INTEGER default 110,
     album_id INTEGER default 295,
     composer TEXT default NULL,
     track INTEGER default 0,
     year INTEGER default 1999,
     is_ringtone INTEGER default 1,
     is_music INTEGER default 1,
     is_alarm INTEGER default 1,
     is_notification INTEGER default 0,
     is_podcast INTEGER default 0,
     album_artist TEXT default NULL,
     duration INTEGER default 120,
     bookmark INTEGER default 1,
     artist TEXT default NULL,
     album TEXT default NULL,
     resolution TEXT default NULL,
     tags TEXT default NULL,
     category TEXT default NULL,
     language TEXT default NULL,
     mini_thumb_data TEXT default NULL,
     name TEXT default NULL,
     media_type INTEGER default 1,
     old_id INTEGER default 1,
     is_drm INTEGER default 1,
     width INTEGER default 1, 
     height INTEGER default 1, 
     title_resource_uri TEXT default NULL,
     owner_package_name TEXT DEFAULT NULL,
     color_standard INTEGER default 1,
     color_transfer INTEGER default 0, 
     color_range INTEGER default 0,
     _hash BLOB DEFAULT NULL,
     is_pending INTEGER DEFAULT 0,
     is_download INTEGER DEFAULT 0,
     download_uri TEXT DEFAULT NULL,
     referer_uri TEXT DEFAULT NULL,
     is_audiobook INTEGER DEFAULT 0,
     date_expires INTEGER DEFAULT NULL,
     is_trashed INTEGER DEFAULT 0,
     group_id INTEGER DEFAULT NULL,
     primary_directory TEXT DEFAULT NULL,
     secondary_directory TEXT DEFAULT NULL,
     document_id TEXT DEFAULT NULL,
     instance_id TEXT DEFAULT NULL,
     original_document_id TEXT DEFAULT NULL,
     relative_path TEXT DEFAULT NULL,
     volume_name TEXT DEFAULT NULL,
     artist_key TEXT DEFAULT NULL,
     album_key TEXT DEFAULT NULL,
     genre TEXT DEFAULT NULL,
     genre_key TEXT DEFAULT NULL,
     genre_id INTEGER DEFAULT 12,
     author TEXT DEFAULT NULL,
     bitrate INTEGER DEFAULT NULL,
     capture_framerate REAL DEFAULT NULL,
     cd_track_number TEXT DEFAULT NULL,
     compilation INTEGER DEFAULT NULL,
     disc_number TEXT DEFAULT NULL,
     is_favorite INTEGER DEFAULT 0,
     num_tracks INTEGER DEFAULT NULL,
     writer TEXT DEFAULT NULL,
     exposure_time TEXT DEFAULT NULL,
     f_number TEXT DEFAULT NULL,
     iso INTEGER DEFAULT NULL,
     scene_capture_type INTEGER DEFAULT NULL,
     generation_added INTEGER DEFAULT 0,
     generation_modified INTEGER DEFAULT 0,
     data BLOB,
     _transcode_status INTEGER DEFAULT 0,
     _video_codec_type TEXT DEFAULT NULL,
     _modifier INTEGER DEFAULT 0,
     is_recording INTEGER DEFAULT 0,
     redacted_uri_id TEXT DEFAULT NULL,
     _user_id INTEGER DEFAULT 0,
     _special_format INTEGER DEFAULT NULL) ROW_FORMAT=DYNAMIC; 
        """)


txtname="texts/test"+sys.argv[1]+".txt"

with open(txtname, "rb") as f:
    payload = f.read()

samptext="THIS IS A WORLD PREMIERE"

conn.autocommit = False

for a in range(0, 100):
    data=[]
    for i in range(0, 500):
        row=[i+a*500, samptext, samptext, i%100, payload]
        data.append(row)

    cursor.execute("START TRANSACTION")
    cursor.executemany("INSERT INTO test(_id, samp, description, mini_thumb_magic, data) VALUES (%s, %s, %s, %s, %s)", data)

conn.commit()
conn.close()

print(f"FINISHED INSERTION. CHECK ls -l and port it to android device")

