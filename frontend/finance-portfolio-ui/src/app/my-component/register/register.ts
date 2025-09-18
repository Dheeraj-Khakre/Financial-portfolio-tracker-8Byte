import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register {
 private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required]
  });

  submit() {
    if (this.form.invalid) return;
    const { username, email, password, firstName, lastName } = this.form.value;
this.auth.register({
  username: username!,
  email: email!,
  password: password!,
  firstName: firstName!,
  lastName: lastName!
}).subscribe({
      next: () => {
        alert('Registered successfully. Please login.');
        this.router.navigate(['/login']);
      },
      error: err => alert(err.error?.message || 'Registration failed')
    });
  }
}
