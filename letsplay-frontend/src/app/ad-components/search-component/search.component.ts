import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
  ValidatorFn
} from '@angular/forms';

import { MenuItem, MessageService } from 'primeng/api';

import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';
import { musicStylesEnum } from '../enums/musicStylesEnum';

interface MetalStyle {
  name: string;
  code: string;
}

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent {
  seeking!: string;
  searchForm!: FormGroup;

  metalStyles!: MetalStyle[];
  selectedMetalStyles!: MetalStyle[];

  ads!: Ad[];

  constructor(private formbuilder: FormBuilder, private adService: AdService, private messageService: MessageService) {
    this.searchForm = this.formbuilder.group({
      search: ['', Validators.required],
      musicianType: [''],
      location: ['', Validators.required],
      selectedMetalStyles: ['', Validators.required],
    }, { validators: this.validateMusicianType() });
    console.log(this.searchForm.controls)
  }

  ngOnInit() {
    this.metalStyles = this.getStylesFromEnum();
  }

  validateMusicianType(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const search = control.get('search')?.value;
      const musicianType = control.get('musicianType')?.value;
  
      const isMusician = search === 'musician';
  
      if (isMusician && !musicianType) {
        return { noMusicianTypeSelected: true };
      }
  
      return null;
    };
  }
  
  

  getStylesFromEnum(): MetalStyle[] {
    return Object.keys(musicStylesEnum).map((key) => ({
      name: musicStylesEnum[key as keyof typeof musicStylesEnum],
      code: key,
    }));
  }

  setSeeking(value: string) {
    this.seeking = value;
  }

  showToastResults(ads: Ad[]) {
    if (ads.length === 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Aucune annonce trouvée',
        detail: 'Aucune annonce ne correspond à ta recherche',
      });
    } else {
      this.messageService.add({
        severity: 'success',
        summary: 'Annonces trouvées',
        detail: 'Nous avons trouvé ' + ads.length + ' annonce(s)',
      });
    }
  }

  onSubmit() {
    const metalGenres = this.selectedMetalStyles.map((style) => style.code);

    this.adService.searchAds
      .call(
        this.adService,
        this.searchForm.value.musicianType,
        metalGenres,
        this.searchForm.value.location
      )
      .subscribe((ads: Ad[]) => {
        this.ads = ads;
        this.showToastResults(ads);
      });
  }
}
