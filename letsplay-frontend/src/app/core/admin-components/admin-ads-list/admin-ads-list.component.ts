import { Component, Input } from '@angular/core';
import { Ad } from '../../models/ad.model';
import { AdService } from '../../services/ad.service';

@Component({
  selector: 'app-admin-ads-list',
  templateUrl: './admin-ads-list.component.html',
  styleUrl: './admin-ads-list.component.css',
})
export class AdminAdsListComponent {
  @Input() ads: Ad[] = [];

  constructor(private adService: AdService) {}

  ngOnInit(): void {
    this.adService.getAllAds().subscribe({
      next: (ads) => {
        this.ads = ads;
      },
      error: (error) => {
        console.error('Error fetching ads:', error);
      },
    });
  }
}
