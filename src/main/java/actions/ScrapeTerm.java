package actions;

import static database.courses.SelectCourseSectionRows.selectCourseSectionRows;

import database.GetConnection;
import database.courses.InsertCourses;
import database.courses.UpdateSections;
import database.epochs.CompleteEpoch;
import database.epochs.GetNewEpoch;
import database.models.CourseSectionRow;
import database.models.SectionID;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import nyu.SubjectCode;
import nyu.Term;
import scraping.ScrapeCatalog;
import search.GetResources;
import search.UpdateIndex;
import utils.Utils;

public class ScrapeTerm {

    public static void
    scrapeTerm(Term term, Integer batchSize, Integer batchSizeSections) {
        scrapeTerm(term, batchSize, batchSizeSections, i -> i);
    }

  public static void
  scrapeTerm(Term term, Integer batchSize, Integer batchSizeSections,
             Function<List<SubjectCode>, Iterable<SubjectCode>> f) {
    GetConnection.withContext(context -> {
      int epoch = GetNewEpoch.getNewEpoch(context, term);
      List<SubjectCode> allSubjects = SubjectCode.allSubjects();

      Iterator<SectionID> s =
          ScrapeCatalog.scrapeFromCatalog(term, f.apply(allSubjects), batchSize)
              .flatMap(courseList
                       -> InsertCourses
                              .insertCourses(context, term, epoch, courseList)
                              .stream())
              .iterator();

      UpdateSections.updateSections(context, term, s, batchSizeSections);

      Stream<CourseSectionRow> rows =
          StreamSupport
              .stream(f.apply(SubjectCode.allSubjects()).spliterator(), false)
              .flatMap(code -> selectCourseSectionRows(context, epoch, code));

      Utils.deleteFile(GetResources.getIndexFileForEpoch(epoch));
      UpdateIndex.updateIndex(epoch, rows);

      CompleteEpoch.completeEpoch(context, term, epoch);
    });
  }
}