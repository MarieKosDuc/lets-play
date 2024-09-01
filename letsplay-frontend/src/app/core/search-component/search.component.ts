import { Component } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors } from '@angular/forms';
import { Validators } from '@angular/forms';

import { MessageService } from 'primeng/api';

import { MusicStylesEnum } from '../enums/musicStylesEnum';
import { MusicianTypesEnum } from '../enums/musicianTypesEnum';
import { LocationsEnum } from '../enums/locationsEnum';

import { Ad } from '../models/ad.model';
import { AdService } from '../services/ad.service';

interface DropdownItems {
  name: string;
  code: string;
}

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
})
export class SearchComponent {
  protected loading: boolean = false;
  protected submitted: boolean = false;
  protected adForm!: FormGroup;

  protected searchedAds!: Ad[];

  // musician type dropdowns settings
  protected fromMusicianTypes: DropdownItems[] = [
    ...Object.keys(MusicianTypesEnum).map((key) => ({
      name:
        key !== 'band'
          ? 'Un ' +
            MusicianTypesEnum[key as keyof typeof MusicianTypesEnum] +
            ' qui recherche un groupe'
          : 'Un ' +
            MusicianTypesEnum[key as keyof typeof MusicianTypesEnum] +
            ' qui recherche un musicien',
      code: key,
    })),
  ];

  // searched musician type dropdowns settings
  protected searchingMusicianTypes: DropdownItems[] = [
    ...Object.keys(MusicianTypesEnum)
      .filter((key) => key !== 'band')
      .map((key) => ({
        name: 'Un ' + MusicianTypesEnum[key as keyof typeof MusicianTypesEnum],
        code: key,
      })),
  ];
  protected isBandSearching: boolean = false;

  // music styles dropdown settings
  protected musicStyles: DropdownItems[] = [
    ...Object.keys(MusicStylesEnum).map((key) => ({
      name: MusicStylesEnum[key as keyof typeof MusicStylesEnum],
      code: key,
    })),
  ];

  // location dropdown settings
  protected locations: DropdownItems[] = [
    ...Object.keys(LocationsEnum).map((key) => ({
      name: LocationsEnum[key as keyof typeof LocationsEnum],
      code: key,
    })),
  ];

  constructor(
    private adService: AdService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.searchedAds = [];

    // Form Group creation
    this.adForm = new FormGroup({
      selectedMusicianTypeFrom: new FormControl<DropdownItems | null>(null),
      selectedSearchingMusicianType: new FormControl<DropdownItems | null>(
        null
      ),
      selectedMusicStyles: new FormControl([], Validators.required),
      selectedLocation: new FormControl('', [Validators.required]),
    });
  }

  setBandSearching(event: any) {
    if (this.adForm.get('selectedMusicianTypeFrom')?.value.code === 'band') {
      this.isBandSearching = true;
    } else {
      this.isBandSearching = false;
    }
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
    const searching =
      this.adForm.get('selectedMusicianTypeFrom')?.value.code === 'band'
        ? this.adForm.get('selectedSearchingMusicianType')?.value?.code ?? ''
        : 'band';
    const selectedMusicStyles =
      this.adForm.get('selectedMusicStyles')?.value ?? [];

    this.adService.searchAds(searching,
        this.adForm.get('selectedMusicianTypeFrom')?.value.code ?? '',
        selectedMusicStyles.map((style: DropdownItems) => style.code),
        this.adForm.get('selectedLocation')?.value.code ?? ''
      )
      .subscribe({
        next: (ads: Ad[]) => {
          this.searchedAds = ads;
          this.transformAds(ads);
          this.showToastResults(ads);
        },
      });
  }

  private transformStyles(styles: string[]): string[] {
    return styles.map(
      (style) => MusicStylesEnum[style as keyof typeof MusicStylesEnum] || style
    );
  }

  private transformLocation(location: string): string {
    return LocationsEnum[location as keyof typeof LocationsEnum] || location;
  }

  private transformAds(ads: Ad[]) {
    for (const ad of ads) {
      ad.styles = this.transformStyles(ad.styles);
      ad.location = this.transformLocation(ad.location);
    }
  }
}
