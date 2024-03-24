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
  id!: number;
  createdAt!: Date;
  title!: string;
  userType!: string;
  seeking!: string;
  image: string | undefined;


  constructor(private router: Router) {}

  ngOnInit(): void {
  }

  goToAd() {
    this.router.navigateByUrl(`ad/${this.ad.id}`);
  }
}
