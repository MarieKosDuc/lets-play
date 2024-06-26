import { Component } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors } from '@angular/forms';
import { Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthStorageService } from 'src/app/shared/services/storage.service';
import { User } from 'src/app/authentication/models/user.model';
import { AdCreation } from '../models/adCreation.model';

import { MusicStylesEnum } from '../enums/musicStylesEnum';
import { MusicianTypesEnum } from '../enums/musicianTypesEnum';
import { LocationsEnum } from '../enums/locationsEnum';

import { CloudinaryService } from 'src/app/layout/cloudinary/cloudinary.service';
import { AdService } from '../ad.service';

import { MessageService } from 'primeng/api';

interface DropdownItems {
  name: string;
  code: string;
}

@Component({
  selector: 'app-ad-create',
  templateUrl: './ad-create.component.html',
  styleUrls: ['./ad-create.component.css'],
})
export class AdCreateComponent {
  protected user!: User | null;

  protected adForm!: FormGroup;

  protected adData!: AdCreation;

  protected loading: boolean = false;
  protected submitted: boolean = false;

  // musician type dropdowns settings
  protected fromMusicianTypes: DropdownItems[] = [
    ...Object.keys(MusicianTypesEnum).map((key) => ({
      name:
        key !== 'band'
          ? 'Un ' +
            MusicianTypesEnum[key as keyof typeof MusicianTypesEnum] +
            ' qui recherche un groupe'
          : 'Un ' +
            MusicianTypesEnum[key as keyof typeof MusicianTypesEnum] +
            ' qui recherche un musicien',
      code: key,
    })),
  ];

  // searched musician type dropdowns settings
  protected searchingMusicianTypes: DropdownItems[] = [
    ...Object.keys(MusicianTypesEnum)
      .filter((key) => key !== 'band')
      .map((key) => ({
        name: 'Un ' + MusicianTypesEnum[key as keyof typeof MusicianTypesEnum],
        code: key,
      })),
  ];
  protected isBandSearching: boolean = false;

  // image settings
  protected myWidget: any;
  protected uploadPreset: String = this.cloudinaryService.getUploadPreset();
  protected myCloudName: String = this.cloudinaryService.getCloudName();

  protected imageSrc: string = '';
  protected baseUrl: String = this.cloudinaryService.getBaseImageURL();

  // music styles dropdown settings
  protected musicStyles: DropdownItems[] = [
    ...Object.keys(MusicStylesEnum).map((key) => ({
      name: MusicStylesEnum[key as keyof typeof MusicStylesEnum],
      code: key,
    })),
  ];

  // location dropdown settings
  protected locations: DropdownItems[] = [
    ...Object.keys(LocationsEnum).map((key) => ({
      name: LocationsEnum[key as keyof typeof LocationsEnum],
      code: key,
    })),
  ];

  constructor(
    private authStorageService: AuthStorageService,
    private cloudinaryService: CloudinaryService,
    private adService: AdService,
    private messageService: MessageService,
    private router: Router
  ) {}

  ngOnInit() {
    this.authStorageService.user$.subscribe((user) => {
      this.user = user;
    });

    // Form Group creation
    this.adForm = new FormGroup({
      title: new FormControl('', [Validators.required]),
      selectedMusicianTypeFrom: new FormControl<DropdownItems | null>(null),
      selectedSearchingMusicianType: new FormControl<DropdownItems | null>(
        null
      ),
      selectedMusicStyles: new FormControl([], Validators.required),
      selectedLocation: new FormControl('', [Validators.required]),
      description: new FormControl('', [Validators.required]),
    });

    // @ts-ignore: Unreachable code error
    this.myWidget = cloudinary.createUploadWidget(
      {
        cloudName: this.myCloudName,
        uploadPreset: this.uploadPreset,
        cropping: true, //add a cropping step
        sources: ['local', 'url'], // restrict the upload sources to URL and local files
        multiple: false, //restrict upload to a single file
        tags: ['users', 'profile'], //add the given tags to the uploaded files
        context: { alt: 'Image de profil' }, //add the given context data to the uploaded files
        maxImageFileSize: 2000000, //restrict file size to less than 2MB
        maxImageWidth: 1000, //Scales the image down to a width of 2000 pixels before uploading
      },
      // @ts-ignore: Unreachable code error
      (error, result) => {
        if (!error && result && result.event === 'success') {
          console.log('Done! Here is the image info: ', result.info);
          this.imageSrc = result.info.secure_url;
        }
      }
    );
  }

  // custom validator for the musician type dropdown
  protected customValidator(group: FormGroup): ValidationErrors | null {
    const selectedMusicianTypeFrom = group.get(
      'selectedMusicianTypeFrom'
    )?.value;
    const selectedSearchingMusicianType = group.get(
      'selectedSearchingMusicianType'
    )?.value;

    if (
      selectedMusicianTypeFrom !== 'band' ||
      (selectedMusicianTypeFrom === 'band' && selectedSearchingMusicianType)
    ) {
      return null; // valid form
    } else {
      return { invalidCombination: true }; // invalid form
    }
  }

  protected openWidget() {
    this.myWidget.open();
  }

  // default image for musician type
  protected setDefaultImage(event: any) {
    if (this.adForm.get('selectedMusicianTypeFrom')?.value) {
      this.imageSrc =
        this.baseUrl +
        this.adForm.get('selectedMusicianTypeFrom')?.value.code +
        '.jpg';
    }
    this.setMusicianSearchedTypeDropdown();
  }

  // make the dropdown appear if the musician type is band
  protected setMusicianSearchedTypeDropdown() {
    if (this.adForm.get('selectedMusicianTypeFrom')?.value.code === 'band') {
      this.isBandSearching = true;
    } else {
      this.isBandSearching = false;
    }
  }

  // creation of the temporary ad object
  protected createTempAd() {
    const searching =
      this.adForm.get('selectedMusicianTypeFrom')?.value.code === 'band'
        ? this.adForm.get('selectedSearchingMusicianType')?.value?.code ?? ''
        : 'band';
    const selectedMusicStyles =
      this.adForm.get('selectedMusicStyles')?.value ?? [];

    this.adData = {
      createdAt: new Date(),
      title: this.adForm.get('title')?.value ?? '',
      userId: this.user?.id ?? '',
      from: this.adForm.get('selectedMusicianTypeFrom')?.value.code ?? '',
      searching: searching,
      image: this.imageSrc,
      styles: selectedMusicStyles.map((style: DropdownItems) => style.code),
      location: this.adForm.get('selectedLocation')?.value.code ?? '',
      description: this.adForm.get('description')?.value ?? '',
    };
  }

  // submit the ad creation form
  protected onSubmit() {
    this.createTempAd();
    this.loading = true;

    this.adService.createAd(this.adData).subscribe(
      (response) => {
        this.loading = false;
        this.submitted = true;
        this.messageService.add({
          severity: 'success',
          summary: 'Annonce créée',
          detail: 'Annonce créée avec succès',
        });
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 2000);
      },
      (error) => {
        this.submitted = false;
        if (error.error.message === 'Ad with this title already exists') {
          this.messageService.add({
            severity: 'error',
            summary: 'Erreur',
            detail: 'Il existe déjà une annonce avec ce titre',
          });
        } else {
          this.messageService.add({
            severity: 'error',
            summary: 'Erreur',
            detail: "Une erreur est survenue lors de la création de l'annonce",
          });
        }
      }
    );
  }
}
