import { Component, Input } from '@angular/core';

import { Router } from '@angular/router';

import { User } from 'src/app/authentication/models/user.model';
import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { AuthStorageService } from 'src/app/shared/services/storage.service';

@Component({
  selector: 'app-liked-ads',
  templateUrl: './liked-ads.component.html',
  styleUrl: './liked-ads.component.css',
})
export class LikedAdsComponent {
  protected user!: User;
  protected noAdsFound: boolean = false;

  @Input() ads: Ad[] = [];

  constructor(
    private adService: AdService,
    private authStorageService: AuthStorageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authStorageService.user$.subscribe((user) => {
      this.user = user;
      this.adService.getUserFavorites(this.user.id).subscribe(
        (ads: Ad[]) => {
          this.ads = ads;
        },
        (error) => {
          if (error.status === 404) {
            this.noAdsFound = true;
          }
        }
      );
    });
  }

  protected goToHome(): void {
    this.router.navigate(['/home']);
  }
}
