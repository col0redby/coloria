package models.db

/*
  Using this class to represent a result returned
  from database after saving processing results.
 */
final case class ProcessingResultSaveResult(
    imageId: Int,
    versionId: Int,
    colorIds: List[Int]
)
