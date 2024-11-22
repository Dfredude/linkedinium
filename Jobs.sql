USE JOB_APPLY_BOT;
SELECT COUNT(*) AS JobsApplied, f.FailedJobApplications FROM jobs as j
	INNER JOIN (SELECT COUNT(*) AS FailedJobApplications FROM pending_jobs) as f
ON true
;