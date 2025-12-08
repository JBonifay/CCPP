# 🚀 GitHub Pages Setup - One-Time Configuration

## ⚠️ **IMPORTANT: Do This BEFORE Pushing the Workflow**

The workflow needs GitHub Pages to be manually enabled first (GitHub Actions tokens don't have permission to create it automatically).

---

## 📋 **Step-by-Step Setup**

### Step 1: Push Your Code (Without Running the Workflow Yet)

```bash
# First, commit and push the workflow files
git add .github/ blog/ README.md PLAN.md INITIAL_CONVERSATION.md
git commit -m "Add CI/CD workflows and documentation"
git push origin main
```

### Step 2: Enable GitHub Pages Manually

1. **Go to your repository on GitHub**
   - Navigate to: `https://github.com/YOUR_USERNAME/CCPP`

2. **Open Settings**
   - Click the **Settings** tab (top right)

3. **Go to Pages**
   - Scroll down in the left sidebar
   - Click **Pages**

4. **Configure Source**
   - Under "Build and deployment"
   - **Source**: Select **GitHub Actions** (NOT "Deploy from a branch")
   - Click **Save** (if button appears)

**That's it!** ✅

---

## 🎯 **After Enabling Pages**

### Option A: Trigger Workflow Manually

1. Go to **Actions** tab
2. Click **Deploy Blog to GitHub Pages** workflow
3. Click **Run workflow** button
4. Select `main` branch
5. Click **Run workflow**

### Option B: Push a Change to Blog

```bash
# Make any change in blog/
echo "# Test" >> blog/README.md
git add blog/
git commit -m "Trigger blog deployment"
git push origin main
```

The workflow will automatically run!

---

## 📍 **Your Blog URL**

After successful deployment, your blog will be at:

```
https://YOUR_GITHUB_USERNAME.github.io/CCPP/
```

Example: `https://joffrey.github.io/CCPP/`

---

## ✅ **Verification**

### Check Deployment Status

1. Go to **Actions** tab
2. Click on the latest "Deploy Blog to GitHub Pages" run
3. Verify all steps are green ✅

### Check Pages Status

1. Go to **Settings** → **Pages**
2. You should see: "Your site is live at https://..."

### Visit Your Blog

- Click the URL or navigate to it directly
- You should see your Astro blog!

---

## 🔧 **Troubleshooting**

### Error: "Resource not accessible by integration"

**Solution**: You need to enable GitHub Pages manually first (see Step 2 above)

### Error: "Not Found"

**Solution**: GitHub Pages isn't enabled yet. Follow Step 2.

### Error: "404 - Page not found" when visiting the blog

**Check these:**

1. **Astro config has correct values** (`blog/astro.config.mjs`):
   ```javascript
   site: process.env.SITE || 'https://YOUR_USERNAME.github.io',
   base: process.env.BASE_PATH || '/CCPP',
   ```

2. **Repository name matches base path**:
   - If repo is named `CCPP`, base should be `/CCPP`
   - If repo is named `ccpp-project`, base should be `/ccpp-project`

3. **Wait a minute**: First deployment can take 1-2 minutes

### Workflow runs but nothing happens

1. Check Pages is set to "GitHub Actions" source
2. Check workflow permissions in Settings → Actions → General
3. Ensure "Read and write permissions" is enabled

---

## 🎨 **Customization**

### Update Your Blog URL

Edit `blog/astro.config.mjs`:

```javascript
export default defineConfig({
  integrations: [mdx()],
  site: process.env.SITE || 'https://YOUR_USERNAME.github.io',
  base: process.env.BASE_PATH || '/YOUR_REPO_NAME',
});
```

Replace:
- `YOUR_USERNAME` → Your GitHub username
- `YOUR_REPO_NAME` → Your repository name (case-sensitive!)

### Test Locally Before Deploying

```bash
cd blog
npm install
npm run build
npm run preview
```

Then visit: `http://localhost:4321/CCPP/`

---

## 📚 **Next Steps After Setup**

Once GitHub Pages is enabled and the workflow works:

1. ✅ Workflow runs automatically on blog changes
2. ✅ Deploy manually via Actions tab anytime
3. ✅ Blog updates within 1-2 minutes
4. ✅ No more manual configuration needed!

---

## 🆘 **Still Having Issues?**

Check the GitHub Actions logs:
1. **Actions** tab → Click failed workflow
2. Expand failed step to see detailed error
3. Common issues:
   - Pages not enabled (see Step 2)
   - Wrong base path in Astro config
   - Missing `.nojekyll` file (should be in `blog/public/`)

---

**Happy Blogging!** 📝✨
