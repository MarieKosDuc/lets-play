import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdImageComponent } from './ad-image.component';

describe('AdImageComponent', () => {
  let component: AdImageComponent;
  let fixture: ComponentFixture<AdImageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdImageComponent]
    });
    fixture = TestBed.createComponent(AdImageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
