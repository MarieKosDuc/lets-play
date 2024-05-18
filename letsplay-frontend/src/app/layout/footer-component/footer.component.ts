import { Component } from '@angular/core';
import * as e from 'cors';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {
  public contactEmail:String = environment.contact_email;
  
}
