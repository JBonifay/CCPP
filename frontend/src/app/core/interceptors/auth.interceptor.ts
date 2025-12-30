import { HttpInterceptorFn } from '@angular/common/http';

const PUBLIC_URLS = ['/auth/login', '/auth/register'];

const isPublicUrl = (url: string): boolean =>
  PUBLIC_URLS.some((publicUrl) => url.includes(publicUrl));

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (isPublicUrl(req.url)) {
    return next(req);
  }

  const token = localStorage.getItem('token');

  if (token) {
    const cloned = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
    return next(cloned);
  }

  return next(req);
};
