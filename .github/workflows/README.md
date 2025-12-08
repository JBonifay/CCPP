# GitHub Actions Workflows

This directory contains CI/CD workflows for the CCPP project.

## Workflows

### 1. Deploy Blog (`deploy-blog.yml`)

**Purpose**: Automatically build and deploy the Astro blog to GitHub Pages

**Triggers**:
- Push to `main` branch when files in `blog/` change
- Manual trigger via workflow_dispatch

**Steps**:
1. Checkout code
2. Setup Node.js 20
3. Install dependencies (`npm ci`)
4. Build Astro site
5. Upload to GitHub Pages
6. Deploy to GitHub Pages

**Setup Required**:
1. Go to Repository Settings → Pages
2. Set Source to "GitHub Actions"
3. The blog will be available at: `https://<username>.github.io/<repo-name>/`

**Environment Variables**:
- `BUILD_PATH`: `./blog` (path to Astro project)

---

### 2. Build and Test (`build-and-test.yml`)

**Purpose**: Build, test, and publish Maven project artifacts

**Triggers**:
- Push to `main` or `develop` branches when Java/Maven files change
- Pull requests to `main` or `develop`
- Manual trigger via workflow_dispatch

**Jobs**:

#### `build`
- Builds all Maven modules
- Runs unit tests
- Generates test reports
- Uploads JAR artifacts (30 days retention)

#### `code-quality`
- Runs `mvn verify`
- Checks code quality (placeholder for JaCoCo coverage)

#### `publish`
- Only runs on `main` branch pushes
- Creates releases for tags
- Archives all JAR files
- Uploads release archive (90 days retention)

**Setup Required**:
1. Ensure `pom.xml` is at repository root
2. All modules must be buildable with Java 25
3. (Optional) Add JaCoCo plugin to `pom.xml` for coverage

**Artifacts**:
- `maven-build-artifacts`: Individual JAR files (30 days)
- `ccpp-release-<commit-sha>`: Complete release archive (90 days)

---

## Status Badges

Add these to your main README.md:

```markdown
[![Deploy Blog](https://github.com/<username>/<repo>/actions/workflows/deploy-blog.yml/badge.svg)](https://github.com/<username>/<repo>/actions/workflows/deploy-blog.yml)
[![Build and Test](https://github.com/<username>/<repo>/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/<username>/<repo>/actions/workflows/build-and-test.yml)
```

---

## Enabling GitHub Pages

To enable the blog deployment:

1. Go to your repository on GitHub
2. Click **Settings**
3. Click **Pages** in the left sidebar
4. Under "Build and deployment":
   - Source: Select **GitHub Actions**
5. The workflow will automatically deploy on the next push to `main`

---

## Manual Workflow Trigger

To manually trigger a workflow:

1. Go to **Actions** tab in your repository
2. Select the workflow (Deploy Blog or Build and Test)
3. Click **Run workflow**
4. Select branch and click **Run workflow**

---

## Secrets and Permissions

### Required Permissions

Both workflows use default `GITHUB_TOKEN` with these permissions:

**deploy-blog.yml**:
- `contents: read`
- `pages: write`
- `id-token: write`

**build-and-test.yml**:
- `contents: read` (default)

### Optional Secrets

If you want to publish to Maven Central or other registries:

1. Go to Settings → Secrets and variables → Actions
2. Add secrets:
   - `MAVEN_USERNAME`
   - `MAVEN_PASSWORD`
   - `GPG_PRIVATE_KEY` (for signing)

---

## Monitoring

- View workflow runs: **Actions** tab
- Check deployment status: **Deployments** section (for Pages)
- Download artifacts: Click on a workflow run → Artifacts section

---

## Troubleshooting

### Blog deployment fails

**Issue**: `Error: No such file or directory: blog/dist`

**Solution**: Ensure `npm run build` succeeds locally:
```bash
cd blog
npm install
npm run build
```

### Maven build fails

**Issue**: `Failed to execute goal ... on project ...`

**Solution**:
1. Check Java version in workflow matches local (currently Java 25)
2. Ensure all modules build locally: `mvn clean install`
3. Check module dependencies in `pom.xml`

### Test failures

**Issue**: Tests pass locally but fail in CI

**Solution**:
- Check for environment-specific configurations
- Review test output in workflow logs
- Ensure tests don't depend on local files/services

---

## Future Enhancements

- [ ] Add JaCoCo for code coverage reporting
- [ ] Add SonarQube analysis
- [ ] Add Docker image building
- [ ] Add deployment to staging environment
- [ ] Add performance testing
- [ ] Add security scanning (Dependabot, CodeQL)
