SELECT COUNT(*) 
FROM files
WHERE
	((_size > 0 AND (media_type = '1')))
	AND
	((is_pending=0 AND is_trashed=0))
ORDER BY (CASE WHEN datetaken > 0 THEN datetaken ELSE date_added*1000 END) DESC;
;
