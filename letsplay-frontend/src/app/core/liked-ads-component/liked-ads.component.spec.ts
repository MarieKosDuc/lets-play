import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LikedAdsComponent } from './liked-ads.component';

describe('LikedAdsComponent', () => {
  let component: LikedAdsComponent;
  let fixture: ComponentFixture<LikedAdsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LikedAdsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LikedAdsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
