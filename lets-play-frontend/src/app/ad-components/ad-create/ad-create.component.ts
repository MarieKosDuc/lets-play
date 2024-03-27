import { Component } from '@angular/core';
import { FormBuilder, FormGroup, NgForm } from '@angular/forms';
import { FormControl, Validators } from '@angular/forms';

import { AuthenticationService } from 'src/app/authentication/services/authentication.service';
import { User } from 'src/app/user/models/user.model';
import { AdCreation } from '../models/adCreation.model';

import { CloudinaryService } from 'src/app/cloudinary/cloudinary.service';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { AdService } from '../services/ad.service';


@Component({
  selector: 'app-ad-create',
  templateUrl: './ad-create.component.html',
  styleUrls: ['./ad-create.component.css']
})
export class AdCreateComponent {

  hasMessage: boolean = false;
  message: String = '';

  user!: User | null;

  adForm!: FormGroup;

  adData!: AdCreation;

  seeking!: string;

  myWidget: any;
  uploadPreset: String = this.cloudinaryService.getUploadPreset();
  myCloudName: String = this.cloudinaryService.getCloudName();

  imageSrc: string = '';
  baseUrl: String = this.cloudinaryService.getBaseImageURL();
  resetImageSelect: any;

  dropdownList = [
    { item_id: 1, item_text: 'Death metal' },
    { item_id: 2, item_text: 'Thrash metal' },
    { item_id: 3, item_text: 'Other' }
  ];;
  selectedItems: string[] = [];
  dropdownSettings = {};


  
  constructor(private authService: AuthenticationService, private cloudinaryService: CloudinaryService,
      private adService: AdService,
     private formbuilder: FormBuilder) {
      this.adForm = this.formbuilder.group({
        title: ['', Validators.required],
        search: ['', Validators.required],
        musicianType: [''],
        location: ['', Validators.required],
        description: ['', Validators.required],
        selectedItems: ['', Validators.required]
      });
     }

  ngOnInit() {
    this.selectedItems = [];
    this.dropdownSettings = {
      singleSelection: false,
      idField: 'item_id',
      textField: 'item_text',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 3,
      allowSearchFilter: true
    };

    this.authService.getCurrentUser().subscribe((user: User | null) => {
      if(user != null){
        this.user = user;
      }
    })

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
        maxImageFileSize: 2000000,  //restrict file size to less than 2MB
        maxImageWidth: 1000, //Scales the image down to a width of 2000 pixels before uploading
      },
        // @ts-ignore: Unreachable code error
      (error, result) => {
        if (!error && result && result.event === "success") {
          console.log("Done! Here is the image info: ", result.info);
          this.imageSrc = result.info.secure_url;
        }
      }
    );
  }

  openWidget() {
    this.myWidget.open();
  }

  setSeeking(value: string) {
    this.seeking = value;
    this.setDefaultImage(value);
  }


  setDefaultImage(value: string) {
    this.imageSrc = this.baseUrl + value + '.jpg';
  }

  onItemSelect(item: any) {
    this.selectedItems.push(item.item_text);
    console.log(this.selectedItems);
  }


  onItemDeSelect(item: any) {
    const index = this.selectedItems.indexOf(item.item_text);
    this.selectedItems.splice(index, 1);
    console.log(this.selectedItems);
  }

  onSelectAll(items: any) {
    this.selectedItems = items.map((item: any) => item.item_text);
    console.log(this.selectedItems);
  }

  onDeSelectAll(items: any) {
    this.selectedItems = [];
    console.log(this.selectedItems);
  }

  onSubmit() {
    const connectedUserId = this.user?.id ?? '';
    const selectedRegion = this.adForm.get('location')?.value ?? '';
    const title = this.adForm.get('title')?.value ?? '';
    const description = this.adForm.get('description')?.value ?? '';
    const musicStyles = this.selectedItems;

    this.adData = {
      createdAt: new Date(),
      title: title,
      userId: connectedUserId,
      seeking: this.seeking,
      image: this.imageSrc,
      styles: musicStyles,
      location: selectedRegion,
      description: description,
    }

    console.log(this.adData);

    this.adService.createAd(this.adData).subscribe(
      (response) => {
        console.log(response);
        this.message = "Annonce créée avec succès";
        this.hasMessage = true;
        setTimeout(() => {
          this.hasMessage = false;
        }, 2000)
      },
      (error) => {
        this.message = "Une erreur est survenue lors de la création de l'annonce : " + error.message;
        console.log(error);
        this.hasMessage = true;
        setTimeout(() => {
          this.hasMessage = false;
        }, 2000)
      }
    )
  }

}