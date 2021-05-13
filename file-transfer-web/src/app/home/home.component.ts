import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  files: File[] = [];

  selectedFile: File = {
    id: -1,
    name: '',
    size: 0,
    type: 'FILE'
  };
  PORT = 80;
  URL = 'localhost';

  constructor(private router: Router, private http: HttpClient) {
  }

  ngOnInit(): void {
    this.loadFiles();
  }


  getHeaders(): HttpHeaders {
    const authCode = localStorage.getItem('auth');
    if (authCode == null) {
      return new HttpHeaders();
    }
    const headers = new HttpHeaders({
      auth: authCode
    });
    return headers;
  }

  logout(): void {
    localStorage.clear();
    this.router.navigateByUrl('login');
  }

  private loadFiles(dirId: string = ''): void {
    this.files = [];
    const URL = '';
    const PORT = '';
    let endpoint = `http://${this.URL}:${this.PORT}/api/files`;
    if (dirId !== '') {
      endpoint += `?dirId=${dirId}`;
    }
    const headers = this.getHeaders();
    this.http.get(endpoint, {headers}).toPromise().then((dataFile: any) => {
      for (let i = 0; i < dataFile.length; i++) {
        const file = {
          id: dataFile[i].id,
          name: dataFile[i].name,
          size: dataFile[i].size,
          type: dataFile[i].type
        };
        this.files.push(file);
      }
    }).catch(err => console.log(err));
    // httpget
  }

  showDetails(file: File): void {
    this.selectedFile = file;
  }

  downloadFile(file: File): void {
    const httpOptions = {
      responseType: 'blob' as 'json',
      headers: this.getHeaders()
    };

    this.http.get(`http://${this.URL}:${this.PORT}/api/files/${file.id}/download`, httpOptions)
      .subscribe((data: any) => {
        const blob = new Blob([data], {type: 'application/pdf'});

        const downloadURL = window.URL.createObjectURL(data);
        const link = document.createElement('a');
        link.href = downloadURL;
        link.download = `${file.name}`;
        link.click();
      });
  }

  navigateOrDownload(file: File): void {
    if (file.type.toLowerCase() === 'file') {
      this.downloadFile(file);
    } else {
      this.loadFiles(`${file.id}`);
    }
  }

  goParentDir(): void {
    if (this.selectedFile.id === -1) {
      return;
    }
    // get parent id
  }

  uploadFile(): void {
    const input = document.getElementById('fileInput');
    if (input == null) {
      return;
    }
    input.click();
  }

  async handleFileInput(event: any): Promise<void> {
    const headers = this.getHeaders();

    if (event.target.files === null || event.target.files.length === 0) {
      return;
    }

    const endpoint = `http://${this.URL}:${(this.PORT)}/api/files`;
    const formData: FormData = new FormData();
    const fileToUpload: string = event.target.files[0];
    formData.append('file', fileToUpload);
    this.http.post<any>(endpoint, formData, {headers}).toPromise().then(_ => alert('Done!')).catch(_ => alert('Not uploaded!'));
  }
}

interface File {
  id: number;
  name: string;
  size: number;
  type: string;
}
