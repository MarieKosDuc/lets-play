import { Component } from '@angular/core';
import { AuthenticationService } from 'src/app/authentication/services/authentication.service';
import { User } from '../models/user.model';

// Import the CloudinaryModule.
import { CloudinaryService } from 'src/app/cloudinary/cloudinary.service';


// Import the Cloudinary classes.
import {Cloudinary, CloudinaryImage} from '@cloudinary/url-gen';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {

  user!: User | null;
  public newPassword: String = '';
  public confirmNewPassword: String = '';
  img!: CloudinaryImage;
  myWidget: any;
  uploadPreset = 'ml_default'; // replace with your own upload preset
  myCloudName: String = this.cloudinaryService.getCloudName();


  constructor(private authService: AuthenticationService, private cloudinaryService: CloudinaryService) {}


  ngOnInit() {
    this.authService.getCurrentUser().subscribe((user: User | null) => {
      this.user = user;
    });
    
    const cld = this.cloudinaryService.getCloudinary();

    console.log(cld);
    console.log(this.uploadPreset);

    // @ts-ignore: Unreachable code error
    this.myWidget = cloudinary.createUploadWidget(
      {
        cloudName: this.myCloudName,
        uploadPreset: this.uploadPreset,
        // cropping: true, //add a cropping step
        // showAdvancedOptions: true,  //add advanced options (public_id and tag)
        sources: [ "local", "url"], // restrict the upload sources to URL and local files
        // multiple: false,  //restrict upload to a single file
        // folder: "user_images", //upload files to the specified folder
        tags: ["users", "profile"], //add the given tags to the uploaded files
        // context: {alt: "user_uploaded"}, //add the given context data to the uploaded files
        // clientAllowedFormats: ["images"], //restrict uploading to image files only
        maxImageFileSize: 2000000,  //restrict file size to less than 2MB
        // maxImageWidth: 2000, //Scales the image down to a width of 2000 pixels before uploading
      },
        // @ts-ignore: Unreachable code error
      (error, result) => {
        if (!error && result && result.event === "success") {
          console.log("Done! Here is the image info: ", result.info);
          // @ts-ignore: Unreachable code error
          document
            .getElementById("profile-pic")
            .setAttribute("src", result.info.secure_url);
        }
      }
    );
  }

  openWidget() {
    this.myWidget.open();
  }

  updateProfile() {
    if (this.newPassword === this.confirmNewPassword && this.user) {
      this.authService.updateUser(this.newPassword, this.user.id).subscribe(() => {
        this.newPassword = '';
        this.confirmNewPassword = '';
      });
    }
  }

  updateProfilePicture(event: any) {
    
    // const file = event.target.files[0];
    // this.authService.updateProfilePicture(file).subscribe(() => {
    //   this.ngOnInit();
    // });
  }
  

}
