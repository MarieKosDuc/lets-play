import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { AuthStorageService } from 'src/app/shared/services/storage.service';
import { User } from '../../authentication/models/user.model';
import { MusicStylesEnum } from '../enums/musicStylesEnum';
import { LocationsEnum } from '../enums/locationsEnum';

@Component({
  selector: 'app-ads-list',
  templateUrl: './ads-list.component.html',
  styleUrls: ['./ads-list.component.css'],
})
export class AdsListComponent implements OnInit {
  @Input() ads: Ad[] = [];

  constructor(
    private adService: AdService,
    private router: Router,
    private authStorageService: AuthStorageService
  ) {}

  protected user!: User | undefined;
  protected isRecap: boolean = false;
  protected isLoading: boolean = true;
  protected noAdsForUser: boolean = false;

  ngOnInit(): void {
    this.user = this.authStorageService.getUser();

    if (this.router.url === '/home') {
      this.getAllAds();
    } else if (this.router.url === '/my-ads') {
      this.isRecap = true;
      this.getUserAds();
    } else {
      this.ads = this.adService.ads;
    }
  }

  getAllAds() {
    this.adService.getAllAds().subscribe(
      (ads: Ad[]) => {
        this.ads = ads;
        this.transformAds(ads);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching ads:', error);
      }
    );
  }

  getUserAds() {
    this.adService.getUserAds(this.user?.id ?? '').subscribe(
      (ads: Ad[]) => {
        this.ads = ads;
        this.transformAds(ads);
        console.log('User ads:', ads);
        this.isLoading = false;
      },
      (error) => {
        console.error('Error fetching user ads:', error);
        if (error.status === 404) {
          this.noAdsForUser = true;
        }
      }
    );
  }

  transformStyles(styles: string[]): string[] {
    return styles.map(style => MusicStylesEnum[style as keyof typeof MusicStylesEnum] || style);
  }

  transformLocation(location: string): string {
    return LocationsEnum[location as keyof typeof LocationsEnum] || location;
  }

  transformAds(ads: Ad[]) {
    for (const ad of ads) {
      ad.styles = this.transformStyles(ad.styles);
      ad.location = this.transformLocation(ad.location);
    }
  }
}
