import { Component, Input } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { CloudinaryService } from 'src/app/layout/cloudinary/cloudinary.service';
import { MessageService } from 'primeng/api';

import { AuthStorageService } from 'src/app/shared/services/storage.service';
import { AuthenticationService } from '../../authentication/services/authentication.service';
import { AdService } from '../ad.service';

import { User } from '../../authentication/models/user.model';
import { Ad } from '../models/ad.model';
import { profileToUpdate } from '../../shared/models/profileToUpdate.model';
import { Subscription, tap } from 'rxjs';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
  @Input() ads: Ad[] = [];

  protected currentUser?: User;
  protected profileUser?: User;
  protected isConnectedUser: boolean = false;
  protected noAdsForUser: boolean = false;
  protected visibleSuppressToast: boolean = false;

  protected newPassword: string = '';
  protected confirmNewPassword: string = '';
  protected userPicture: string = '';

  protected passwordRegex =
    /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/;

  protected myWidget: any;
  protected uploadPreset: String = this.cloudinaryService.getUploadPreset();
  protected myCloudName: String = this.cloudinaryService.getCloudName();

  private loggedInSubscription: Subscription | undefined;
  private currentUserSubscription: Subscription | undefined;

  constructor(
    private cloudinaryService: CloudinaryService,
    private authStorageService: AuthStorageService,
    private authService: AuthenticationService,
    private adService: AdService,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private router: Router
  ) {}

  ngOnInit():void {

    this.loggedInSubscription = this.authService.isLoggedIn().subscribe((loggedIn: boolean) => {
      if (!loggedIn) {
        // Handle logic when user is not logged in (e.g., redirect or clear profile data)
      }
    });

    this.currentUserSubscription = this.authService.getCurrentUser().subscribe((user) => {
      if (!user) {
        // Handle logic when current user is null (e.g., clear profile data)
      }
    });

    this.authStorageService.user$.subscribe((user) => {
      this.currentUser = user;
      this.checkIfConnectedUserIsProfileUser() 
    });

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

  ngOnDestroy() {
   if (this.loggedInSubscription) {
      this.loggedInSubscription.unsubscribe();
    }
    if (this.currentUserSubscription) {
      this.currentUserSubscription.unsubscribe();
    }
  }

  protected openWidget() {
    this.myWidget.open();
  }

  protected getUserAds(id: string) {
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

  protected onSubmit(form: NgForm){
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

  protected updateProfilePicture(pictureUrl: string) {
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

  protected updatePassword(password: string) {
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

  protected showConfirmSuppressAccount() {
    console.log('showConfirmSuppressAccount');
    console.log('this.visibleSuppressToast:', this.visibleSuppressToast)
    if (!this.visibleSuppressToast) {
      this.messageService.add({
        key: 'confirm-account-deletion',
        sticky: true,
        severity: 'info',
        summary: 'Es-tu sûr.e de vouloir supprimer ton compte ? Cette suppression est irréversible.',
      });
      console.log(this.messageService.messageObserver)
      this.visibleSuppressToast = true;
    }
  }

  protected onConfirm() {
    this.deleteUser();
    this.messageService.clear('confirm-account-deletion');
    this.visibleSuppressToast = false;
  }

  protected onReject() {
    this.messageService.clear('confirm-account-deletion');
    this.visibleSuppressToast = false;
  }

  protected deleteUser() {
    console.log('delete user');
    this.authService.deleteUser(this.currentUser?.id || '').subscribe(
      data => {
        this.authStorageService.clean();
        this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Compte supprimé' });
        this.router.navigate(['/']);
      },
      error => {
        console.error('Error deleting user:', error);
        this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Erreur lors de la suppression du compte' });
      }
    );

  }

  private checkIfConnectedUserIsProfileUser() {
    if (this.currentUser?.id === this.activatedRoute.snapshot.params['id']) {
      this.isConnectedUser = true;
      this.profileUser = this.currentUser;
      this.getUserAds(this.profileUser?.id || '');
    } else {
      this.loadProfileUser(this.activatedRoute.snapshot.params['id']);
    }
  }

  private async loadProfileUser(userId: string) {
    try {
      const userFetched = await this.authService.getUserById(userId).toPromise();
      console.log('User fetched:', userFetched);
      this.profileUser = userFetched;
      this.getUserAds(this.profileUser?.id || '');
    } catch (error) {
      console.error('Error fetching user:', error);
      // this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Erreur lors de la récupération de l\'utilisateur' });
    }
  }
}
