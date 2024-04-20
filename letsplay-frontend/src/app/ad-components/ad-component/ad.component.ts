import { Component, OnInit, Input } from '@angular/core';
import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import {Router, ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-ad',
  templateUrl: './ad.component.html',
  styleUrls: ['./ad.component.css']
})
export class AdComponent implements OnInit{
  @Input() ad!: Ad;
  @Input() truncated = true;
  isSingleAd!: boolean;

  constructor(private router: Router, private route: ActivatedRoute, private adService: AdService) {}

  ngOnInit(): void {
    const currentRoute = this.route.snapshot.routeConfig?.path;
    if (currentRoute === 'home') {
      this.isSingleAd = false;
      this.ad.description=this.truncateText(this.ad.description);
    }

    if (currentRoute === 'ad/:id') {
      this.isSingleAd = true;
      const adId = this.route.snapshot.params['id'];
      console.log(adId);
      this.adService.getAdById(adId).subscribe((ad: Ad) => {
        console.log(ad)
        this.ad = ad;
      });
    }

  }

  truncateText(text: string): string {
      const words = text.split(' ');
      const truncatedText = words.slice(0, 30) 
      return truncatedText.join(' ') + '...';

  }

  goToAd() {
    this.router.navigateByUrl(`ad/${this.ad.id}`);
  }

  mailTo() {
    //window.open(`mailto:${this.ad.email}`);
  }

}
