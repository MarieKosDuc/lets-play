import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router'; // Import ActivatedRoute module

import { environment } from 'src/environments/environment';

import { AuthenticationService } from '../services/authentication.service';

@Component({
  selector: 'app-account-verify',
  templateUrl: './account-verify.component.html',
  styleUrl: './account-verify.component.css'
})
export class AccountVerifyComponent {
  protected isValidationSuccess: boolean = false;
  protected mailto: string = environment.contact_email;
  protected token: string = '';

  constructor(
    private authService: AuthenticationService,
    private route: ActivatedRoute // Inject ActivatedRoute module
  ) { }

  ngOnInit(): void {
    this.token = this.route.snapshot.params['token'];
    console.log(this.token)

    this.authService.verifyAccount(this.token).subscribe(
      (response) => {
        this.isValidationSuccess = true;
      },
      (error) => {
        this.isValidationSuccess = false;
      }
    );
  }



}
