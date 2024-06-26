import { Component, Input } from '@angular/core';
import { User } from 'src/app/authentication/models/user.model';
import { Ad } from '../models/ad.model';
import { AdService } from '../ad.service';
import { AuthStorageService } from 'src/app/shared/services/storage.service';

@Component({
  selector: 'app-liked-ads',
  templateUrl: './liked-ads.component.html',
  styleUrl: './liked-ads.component.css'
})
export class LikedAdsComponent {

  protected user!: User;

  @Input() ads: Ad[] = [];

  constructor(private adService: AdService, private authStorageService: AuthStorageService) { }

  ngOnInit(): void {
    this.authStorageService.user$.subscribe((user) => {
      this.user = user;
      this.adService.getUserFavorites(this.user.id).subscribe((ads: Ad[]) => {
        this.ads = ads;
      });
    });
  }

}
