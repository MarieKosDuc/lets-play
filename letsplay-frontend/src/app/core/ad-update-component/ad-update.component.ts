import { Component, Input } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

import { MessageService } from 'primeng/api';

import { CloudinaryService } from 'src/app/layout/cloudinary/cloudinary.service';
import { AuthStorageService } from 'src/app/shared/services/storage.service';

import { AdService } from '../ad.service';
import { User } from 'src/app/authentication/models/user.model';
import { AdCreation } from '../models/adCreation.model';
import { Ad } from '../models/ad.model';
import { MusicStylesEnum } from '../enums/musicStylesEnum';
import { LocationsEnum } from '../enums/locationsEnum';
import { MusicianTypesEnum } from '../enums/musicianTypesEnum';

interface DropdownItems {
  name: string;
  code: string;
}

@Component({
  selector: 'app-ad-update',
  templateUrl: './ad-update.component.html',
  styleUrl: './ad-update.component.css',
})
export class AdUpdateComponent {
  @Input() ad!: Ad;

  protected loading: boolean = false;
  protected submitted: boolean = false;

  protected user!: User | null;

  protected adForm!: FormGroup;
  protected adData!: AdCreation;

  // image settings
  protected myWidget: any;
  protected uploadPreset: String = this.cloudinaryService.getUploadPreset();
  protected myCloudName: String = this.cloudinaryService.getCloudName();

  protected imageSrc: string = '';
  protected baseUrl: String = this.cloudinaryService.getBaseImageURL();

  protected from: string = '';
  protected searching: string = '';
  protected location: string = '';

  // music styles dropdown settings
  protected musicStyles: DropdownItems[] = [
    ...Object.keys(MusicStylesEnum).map((key) => ({
      name: MusicStylesEnum[key as keyof typeof MusicStylesEnum],
      code: key,
    })),
  ];

  constructor(
    private authStorageService: AuthStorageService,
    private cloudinaryService: CloudinaryService,
    private adService: AdService,
    private messageService: MessageService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.authStorageService.user$.subscribe((user) => {
      this.user = user;
    });

    const adId = this.route.snapshot.params['id'];

    this.adService.getAdById(adId).subscribe((ad: Ad) => {
      this.ad = ad;
      this.adForm.get('title')?.setValue(this.ad.title);
      this.adForm.get('description')?.setValue(this.ad.description);
      this.from = 'un ' + MusicianTypesEnum[this.ad.from as keyof typeof MusicianTypesEnum];
      this.searching = 'qui recherche un ' + MusicianTypesEnum[this.ad.searching as keyof typeof MusicianTypesEnum];
      this.location = LocationsEnum[this.ad.location as keyof typeof LocationsEnum];
      if (this.ad.image) {
        this.imageSrc = this.ad.image;
      }
    });

    // Form Group creation
    this.adForm = new FormGroup({
      title: new FormControl('', [Validators.required]),
      selectedMusicStyles: new FormControl([], Validators.required),
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

  protected openWidget() {
    this.myWidget.open();
  }

  protected createTempAd() {
    const selectedMusicStyles =
      this.adForm.get('selectedMusicStyles')?.value ?? [];

    if (this.user) {
      this.adData = {
        userId: this.user.id,
        createdAt: this.ad.createdAt,
        from: this.ad.from,
        searching: this.ad.searching,
        styles: selectedMusicStyles.map((style: DropdownItems) => style.code),
        location: this.ad.location,
        title: this.adForm.get('title')?.value,
        description: this.adForm.get('description')?.value,
        image: this.imageSrc,
      };
    }
  }

  protected onSubmit() {
    this.createTempAd();
    this.loading = true;

    this.adService.updateAd(this.adData, this.ad.id).subscribe(
      (response) => {
        this.loading = false;
        this.submitted = true;
        this.messageService.add({
          severity: 'success',
          summary: 'Annonce modifiée',
          detail: 'Annonce modifiée avec succès',
        });
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 2000);
      },
      (error) => {
        this.submitted = false;
        this.loading = false;
      
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: "Une erreur est survenue lors de la création de l'annonce",
        });
      }
    );
  }
}
