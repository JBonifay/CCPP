# ⚡ Quick Start Guide

## 🎯 First Time Setup (One Time Only)

### 1. Enable GitHub Pages

**IMPORTANT: Do this BEFORE the workflow runs!**

1. Go to: `https://github.com/YOUR_USERNAME/CCPP/settings/pages`
2. Under "Build and deployment"
3. **Source**: Select **"GitHub Actions"**
4. Click Save

That's it! ✅

### 2. Customize Your Blog URLs

Edit `blog/astro.config.mjs` (lines 9-10):

```javascript
site: process.env.SITE || 'https://YOUR_USERNAME.github.io',
base: process.env.BASE_PATH || '/CCPP',
```

Replace `YOUR_USERNAME` with your GitHub username.

### 3. Deploy

```bash
# Commit everything
git add -A
git commit -m "Setup CI/CD and blog deployment"
git push origin main
```

---

## 🚀 Your Blog Will Be At

```
https://YOUR_USERNAME.github.io/CCPP/
```

---

## 📖 Detailed Guides

- **GitHub Pages Setup**: See [GITHUB_PAGES_SETUP.md](GITHUB_PAGES_SETUP.md)
- **Architecture Plan**: See [PLAN.md](PLAN.md)
- **Workflow Docs**: See [.github/workflows/README.md](.github/workflows/README.md)

---

## 🔍 Quick Commands

```bash
# Build and test locally
mvn clean install

# Run blog locally
cd blog && npm install && npm run dev

# Build blog for production
cd blog && npm run build && npm run preview
```

---

## ✅ Verification

After pushing:

1. Go to **Actions** tab → Check workflow status
2. Go to **Settings → Pages** → Check deployment URL
3. Visit your blog URL

---

**Need Help?** Check [GITHUB_PAGES_SETUP.md](GITHUB_PAGES_SETUP.md) for troubleshooting!
