package lk.ijse.dep11.api;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lk.ijse.dep11.to.reruest.LecturerRequestTo;
import lk.ijse.dep11.to.responce.LecturerResponseTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.serviceloader.AbstractServiceLoaderBasedFactoryBean;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.Binding;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.sql.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/v1/lecturers")
@CrossOrigin
public class LecturerHttpController {

    @Autowired
    private DataSource pool;
    @Autowired
    private Bucket bucket;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public LecturerResponseTO createNewLecturer(@ModelAttribute @Valid LecturerRequestTo lecturer){

        try(Connection connection = pool.getConnection()){
            connection.setAutoCommit(false);
            try{
                PreparedStatement stmInsertLecturer = connection
                        .prepareStatement("INSERT INTO lecturer " +
                                "(name, designation, qualifications, linkedin) " +
                                "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                stmInsertLecturer.setString(1, lecturer.getName());
                stmInsertLecturer.setString(2, lecturer.getDesignation());
                stmInsertLecturer.setString(3, lecturer.getQualifications());
                stmInsertLecturer.setString(4, lecturer.getLinkedin());
                stmInsertLecturer.executeUpdate();

                ResultSet generatedKeys = stmInsertLecturer.getGeneratedKeys();
                generatedKeys.next();
                int lecturerId = generatedKeys.getInt(1);
                String picture = lecturerId + "-" + lecturer.getName();

            if(lecturer.getPicture() != null || lecturer.getPicture().isEmpty()){
                PreparedStatement stmUpdateLecturer = connection
                        .prepareStatement("UPDATE lecturer SET picture = ? WHERE id = ?");
                stmUpdateLecturer.setString(1, picture);
                stmUpdateLecturer.setInt(2,lecturerId);
                stmUpdateLecturer.executeUpdate();

            }

                final String table = lecturer.getType().equalsIgnoreCase("full-time")
                        ? "full_time_rank" : "part_time_rank";
                Statement stm = connection.createStatement();
                ResultSet rst = stm.executeQuery("SELECT `rank` FROM " + table + " ORDER BY `rank` DESC LIMIT 1");
                int rank;
                if (!rst.next()) rank = 1;
                else rank = rst.getInt("rank") + 1;
                PreparedStatement stmInsertRank = connection
                        .prepareStatement("INSERT INTO " + table + " (lecturer_id, `rank`) VALUES (?, ?)");
                stmInsertRank.setInt(1, lecturerId);
                stmInsertRank.setInt(2, rank);
                stmInsertRank.executeUpdate();

                String pictureUrl = null;
               if (lecturer.getPicture() != null && !lecturer.getPicture().isEmpty()){
                   Blob blob = bucket.create(picture, lecturer.getPicture().getInputStream(),
                           lecturer.getPicture().getContentType());
                   pictureUrl = blob
                           .signUrl(1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature())
                           .toString();

               }
                connection.commit();
                return new LecturerResponseTO(lecturerId,
                        lecturer.getName(),
                        lecturer.getDesignation(),
                        lecturer.getQualifications(),
                        lecturer.getType(),
                        pictureUrl,
                        lecturer.getLinkedin());
            } catch (Throwable t) {
                connection.rollback();
                throw t;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/{lecturer-id}")
    public  void  updateLecturerDetails(@PathVariable("lecturer-id") String parameter){
        System.out.println("updateLecturer()");
    }

    @DeleteMapping("/{lecturer-id}")
    public void deleteLecturer(@PathVariable("lecturer-id") int lecturerId){
        System.out.println("deleteLecturer()");
    }

    @GetMapping
    public void getAllLecturers(){

        System.out.println("getAllLecturers()");
    }


}
