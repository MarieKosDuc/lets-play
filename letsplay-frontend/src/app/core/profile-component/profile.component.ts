import { Component, Input } from '@angular/core';
import { AuthStorageService } from 'src/app/shared/services/storage.service';
import { AuthenticationService } from '../../authentication/services/authentication.service';
import { User } from '../../authentication/models/user.model';

import { CloudinaryService } from 'src/app/layout/cloudinary/cloudinary.service';
import { Ad } from '../models/ad.model';
import { AdService } from '../ad.service';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { profileToUpdate } from '../../shared/models/profileToUpdate.model';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent {
  @Input() ads: Ad[] = [];

  protected currentUser!: User | null; // Attention à la différenciation entre utilisateur connecté et profil d'un autre utilisateur
  protected profileUser!: User | null;
  protected isConnectedUser: boolean = false;
  protected noAdsForUser: boolean = false;

  protected newPassword: string = '';
  protected confirmNewPassword: string = '';
  protected userPicture: string = '';

  protected passwordRegex =
    /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/;

  myWidget: any;
  uploadPreset: String = this.cloudinaryService.getUploadPreset();
  myCloudName: String = this.cloudinaryService.getCloudName();

  constructor(
    private cloudinaryService: CloudinaryService,
    private authStorageService: AuthStorageService,
    private authService: AuthenticationService,
    private adService: AdService,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService
  ) {}

  ngOnInit() {
  
    this.authStorageService.user$.subscribe((user) => {
      this.currentUser = user;

      this.userPicture = user?.profilePicture || '';
    });

    if (this.currentUser?.id === this.activatedRoute.snapshot.params['id']) {
      this.isConnectedUser = true;
      this.profileUser = this.currentUser;
    } else {
      this.authService.getUserById(this.activatedRoute.snapshot.params['id']).subscribe(
        (user: User) => {
          this.profileUser = user;
        },
        (error) => {
          console.error('Error fetching user:', error);
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Erreur lors de la récupération de l\'utilisateur' });
        }
      );
    }

    this.getUserAds(this.profileUser?.id || '');

    const cld = this.cloudinaryService.getCloudinary();

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
        // clientAllowedFormats: ["images"], //restrict uploading to image files only
        maxImageFileSize: 2000000, //restrict file size to less than 2MB
        maxImageWidth: 1000, //Scales the image down to a width of 2000 pixels before uploading
      },
      // @ts-ignore: Unreachable code error
      (error, result) => {
        if (!error && result && result.event === 'success') {
          console.log('Done! Here is the image info: ', result.info);
          this.userPicture = result.info.secure_url;

          this.updateProfilePicture(this.userPicture);
        }
      }
    );
  }

  openWidget() {
    this.myWidget.open();
  }

  getUserAds(id: string) {
    this.adService.getUserAds(id).subscribe(
      (ads: Ad[]) => {
        this.ads = ads;
      },
      (error) => {
        console.error('Error fetching ads:', error);
        if (error.status === 404) {
          this.noAdsForUser = true;
        }
      }
    );
  }

  onSubmit(form: NgForm){
    const password = form.value.newPassword;

    if (!this.passwordRegex.test(password)) {
      console.error('Invalid password');
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail:
          'Le mot de passe doit contenir au moins 8 caractères, une lettre, un chiffre et un caractère spécial',
      });
      return;
    }

    if (password !== form.value.confirmNewPassword) {
      console.error('Invalid password');
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Les mots de passe ne correspondent pas' });
      return;
    }

    this.updatePassword(password);
  }

  updateProfilePicture(pictureUrl: string) {
    if(this.currentUser) {

      const request: profileToUpdate = {
        name: this.currentUser.username,
        profilePicture: pictureUrl,
      };

      this.authService.updateUser(this.currentUser.id, request).subscribe(
        (user: User) => {
          this.authStorageService.saveUser(user);
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Image de profil mise à jour' });
        },
        (error) => {
          console.error('Error updating user:', error);
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Erreur lors de la mise à jour de l\'image de profil' });
        }
      );

    }
  }

  updatePassword(password: string) {
    if (this.currentUser) {
      this.authService.updatePassword(this.currentUser.id, password).subscribe(
        data => {
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Mot de passe mis à jour' });
        },
        error => {
          console.error('Error updating password:', error);
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Erreur lors de la mise à jour du mot de passe' });
        }
      );
    }
  }
}
