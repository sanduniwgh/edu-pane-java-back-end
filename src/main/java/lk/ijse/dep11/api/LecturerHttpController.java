package lk.ijse.dep11.api;

import lk.ijse.dep11.to.reruest.LecturerRequestTo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/lecturers")
@CrossOrigin
public class LecturerHttpController {

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public  void createNewLecturer(@ModelAttribute @Valid LecturerRequestTo lecturer){
        System.out.println(lecturer);

        System.out.println("createLecturer()");
    }

    @PatchMapping("/{lecturer-id}")
    public  void  updateLecturerDetails(){
        System.out.println("updateLecturer()");
    }

    @DeleteMapping("/{lecturer-id}")
    public void deleteLecturer(){
        System.out.println("deleteLecturer()");
    }

    @GetMapping
    public void getAllLecturers(){

        System.out.println("getAllLecturers()");
    }


}
