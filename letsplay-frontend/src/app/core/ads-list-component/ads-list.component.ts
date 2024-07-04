import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Subscription } from 'rxjs';

import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { AuthStorageService } from 'src/app/shared/services/storage.service';
import { User } from '../../authentication/models/user.model';
import { MusicStylesEnum } from '../enums/musicStylesEnum';
import { LocationsEnum } from '../enums/locationsEnum';
import { EventBusService } from '../../shared/services/event-bus.service';

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
    private authStorageService: AuthStorageService,
    private eventBusService: EventBusService,
  ) {}

  protected user!: User | undefined;
  protected isRecap: boolean = false;
  protected isLoading: boolean = true;
  protected noAdsForUser: boolean = false;

  private eventBusSubscription?: Subscription;

  ngOnInit(): void {
    this.user = this.authStorageService.getUser();

    this.eventBusSubscription = this.eventBusService.on('logout', () => {
      this.getAllAds();
    });

    if (this.router.url === '/home') {
      this.getAllAds();
    } else if (this.router.url === '/my-ads') {
      this.isRecap = true;
      this.getUserAds();
    } else {
      this.ads = this.adService.ads;
    }

    this.transformAds(this.ads);
  }

  getAllAds() {
    this.adService.getAllAds().subscribe({
      next: (ads) => {
        this.ads = ads;
        this.transformAds(ads);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors de la récupération des annonces', error);
        this.isLoading = false;
      },
    });
  }

  getUserAds() {
    this.adService.getUserAds(this.user?.id ?? '').subscribe({
      next: (ads) => {
        this.ads = ads;
        this.transformAds(ads);
        this.isLoading = false;
      },
      error: (error) => {
        console.error(
          'Erreur lors de la récupération des annonces de l’utilisateur',
          error
        );
        this.isLoading = false; 
      },
    });
  }

  transformStyles(styles: string[]): string[] {
    return styles.map(
      (style) => MusicStylesEnum[style as keyof typeof MusicStylesEnum] || style
    );
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
