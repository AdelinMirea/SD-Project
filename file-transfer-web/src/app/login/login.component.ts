import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {SettingsService} from '../settings.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  email = '';
  password = '';

  URL = 'localhost';

  constructor(private router: Router, private http: HttpClient, private settingService: SettingsService) { }

  ngOnInit(): void {
    const auth = localStorage.getItem('auth');
    if (auth !== null) {
      this.router.navigateByUrl('home');
    }
  }

  login(): void {
    console.log(this.settingService.settings);
    const data = {
      email: this.email,
      password: this.password
    };

    const url = `http://${this.URL}/api/users`;
    this.http.get(url).toPromise().then(dataL => console.log(dataL)).catch(err => console.log(err));


    const url1 = `http://${this.URL}/api/login`;
    this.http.post(url1, data).toPromise().then((dataC: any) => {
      console.log(dataC);
      localStorage.setItem('auth', dataC);
      this.router.navigateByUrl('home');

    }).catch(err => {
      console.log(err);
      alert(err);
    });

  }

  signUp(): void {
    const data = {
      email: this.email,
      password: this.password
    };

    const url = `http://${this.URL}/api/users`;
    this.http.post(url, data).toPromise().then(dataC => console.log(dataC)).catch(err => console.log(err));
  }
}
