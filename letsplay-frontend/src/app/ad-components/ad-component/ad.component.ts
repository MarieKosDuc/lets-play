import { Component, OnInit, Input } from '@angular/core';
import { Location } from '@angular/common';
import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { Router, ActivatedRoute } from '@angular/router';
import { musicStylesEnum } from '../enums/musicStylesEnum';

@Component({
  selector: 'app-ad',
  templateUrl: './ad.component.html',
  styleUrls: ['./ad.component.css'],
})
export class AdComponent implements OnInit {
  @Input() ad!: Ad;
  @Input() truncated = true;
  isSingleAd!: boolean;
  musicStylesEnum = musicStylesEnum;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private adService: AdService,
    private _location: Location
  ) {}

  ngOnInit(): void {
    const currentRoute = this.route.snapshot.routeConfig?.path;
    if (currentRoute === 'home') {
      this.isSingleAd = false;
      this.ad.description = this.truncateText(this.ad.description);
    }

    if (currentRoute === 'ad/:id') {
      this.isSingleAd = true;
      const adId = this.route.snapshot.params['id'];
      this.adService.getAdById(adId).subscribe((ad: Ad) => {
        this.ad = ad;
      });
    }

    this.ad.styles = this.ad.styles.map(
      (style: string) => (musicStylesEnum as any)[style]
    );
  }

  truncateText(text: string): string {
    const words = text.split(' ');
    if (words.length <= 30) {
      return text;
    }
    const truncatedText = words.slice(0, 30);
    return truncatedText.join(' ') + '...';
  }

  goToAd() {
    this.router.navigateByUrl(`ad/${this.ad.id}`);
  }

  back() {
    this._location.back();
  }

  mailTo() {
    //window.open(`mailto:${this.ad.email}`);
  }
}
