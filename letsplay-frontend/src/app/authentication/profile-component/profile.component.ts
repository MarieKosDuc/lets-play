import { Component } from '@angular/core';
import { AuthenticationService } from 'src/app/authentication/services/authentication.service';
import { User } from '../models/user.model';

import { CloudinaryService } from 'src/app/layout/cloudinary/cloudinary.service';
import {CloudinaryImage} from '@cloudinary/url-gen';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {

  user!: User | null;
  public newPassword: String = '';
  public confirmNewPassword: String = '';
  myWidget: any;
  uploadPreset: String = this.cloudinaryService.getUploadPreset();
  myCloudName: String = this.cloudinaryService.getCloudName();


  constructor(private authService: AuthenticationService, private cloudinaryService: CloudinaryService) {}


  ngOnInit() {
    this.authService.getCurrentUser().subscribe((user: User | null) => {
      this.user = user;
    });
    
    const cld = this.cloudinaryService.getCloudinary();


    // @ts-ignore: Unreachable code error
    this.myWidget = cloudinary.createUploadWidget(
      {
        cloudName: this.myCloudName,
        uploadPreset: this.uploadPreset,
        cropping: true, //add a cropping step
        sources: [ "local", "url"], // restrict the upload sources to URL and local files
        multiple: false,  //restrict upload to a single file
        tags: ["users", "profile"], //add the given tags to the uploaded files
        context: {alt: "Image de profil"}, //add the given context data to the uploaded files
        // clientAllowedFormats: ["images"], //restrict uploading to image files only
        maxImageFileSize: 2000000,  //restrict file size to less than 2MB
        maxImageWidth: 1000, //Scales the image down to a width of 2000 pixels before uploading
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

  updateProfile() { //TODO : ne pas updater le mot de passe si vide
    if (this.newPassword === this.confirmNewPassword && this.user) {
      this.authService.updateUser(this.newPassword, this.user.id).subscribe(() => {
        this.newPassword = '';
        this.confirmNewPassword = '';
      });
    }
  }

}
