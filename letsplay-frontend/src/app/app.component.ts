import { Component } from '@angular/core';
import { initFlowbite } from 'flowbite';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [MessageService],
})
export class AppComponent {
  title = 'lets-play';

  constructor() {}

  ngOnInit() {
    initFlowbite();
  }
}
