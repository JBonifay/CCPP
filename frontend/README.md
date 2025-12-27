# CCPP Frontend

Angular web application for the Content Creator Planning Platform.

## Tech Stack

- **Framework**: Angular 21
- **Language**: TypeScript 5.9
- **Testing**: Vitest
- **Build**: Angular CLI
- **Production Server**: Nginx

## Development

### Prerequisites

- Node.js 20+
- npm 11.6+

### Setup

```bash
# Install dependencies
npm install

# Start development server (with API proxy)
npm start
```

The app will run on `http://localhost:4200` with API requests proxied to `http://localhost:8761` (API Gateway).

### Available Scripts

- `npm start` - Start development server with proxy
- `npm run build` - Build for production
- `npm test` - Run tests
- `npm run watch` - Build in watch mode

## Architecture

### API Communication

**Development:**
- Angular runs on `localhost:4200`
- API requests to `/api/*` are proxied to API Gateway on `localhost:8761`
- Configuration: `proxy.conf.json`

**Production:**
- Nginx serves static files
- Traefik routes `/api/*` to API Gateway
- No CORS issues (same domain)

### Environment Configuration

**Development** (`src/environments/environment.development.ts`):
```typescript
apiUrl: '/api' // Proxied to localhost:8761
```

**Production** (`src/environments/environment.ts`):
```typescript
apiUrl: '/api' // Routed by Traefik to api-gateway
```

## Docker Build

The Dockerfile uses multi-stage builds:

1. **Stage 1**: Build Angular app with Node.js
2. **Stage 2**: Serve with Nginx Alpine

```bash
# Build image
docker build -t ccpp-frontend .

# Run container
docker run -p 80:80 ccpp-frontend
```

## Deployment

The frontend is automatically built and published to GitHub Container Registry on push to main.

**Image**: `ghcr.io/jbonifay/ccpp-frontend:latest`

**Deployment flow:**
1. Push code to main branch
2. GitHub Actions builds and tests
3. Docker image is built and pushed to GHCR
4. Pull image on VPS and restart with `docker-compose up -d`

## Project Structure

```
frontend/
├── src/
│   ├── app/              # Application components
│   ├── environments/     # Environment configurations
│   ├── index.html        # HTML entry point
│   ├── main.ts           # Application bootstrap
│   └── styles.css       # Global styles
├── nginx.conf            # Production Nginx config
├── proxy.conf.json       # Development proxy config
├── Dockerfile            # Multi-stage Docker build
├── angular.json          # Angular CLI configuration
└── package.json          # Dependencies and scripts
```

## Contributing

This is a portfolio/learning project. See main README for license information.
