const express = require('express');
const path = require('path');
const fs = require('fs');
const archiver = require('archiver');

const app = express();
const PORT = 5000;

app.use(express.static('public'));

app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.get('/download-project', (req, res) => {
  res.setHeader('Content-Type', 'application/zip');
  res.setHeader('Content-Disposition', 'attachment; filename=NewsTWA-Android-Project.zip');

  const archive = archiver('zip', { zlib: { level: 9 } });
  archive.pipe(res);

  const filesToInclude = [
    'build.gradle',
    'settings.gradle',
    'gradle.properties',
    'gradlew',
    'app/build.gradle',
    'app/proguard-rules.pro',
    'app/src/main/AndroidManifest.xml',
    'app/src/main/java/com/news/twa/MainActivity.java',
    'app/src/main/java/com/news/twa/SplashActivity.java',
    'app/src/main/res/layout/activity_main.xml',
    'app/src/main/res/layout/activity_splash.xml',
    'app/src/main/res/values/strings.xml',
    'app/src/main/res/values/colors.xml',
    'app/src/main/res/values/themes.xml',
    'app/src/main/res/drawable/ic_no_internet.xml',
    'app/src/main/res/drawable/ic_launcher_foreground.xml',
    'app/src/main/res/drawable/ic_launcher_background.xml',
    'app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml',
    'app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml',
    'app/src/main/res/xml/network_security_config.xml',
    'gradle/wrapper/gradle-wrapper.properties',
    '.github/workflows/build-release.yml'
  ];

  filesToInclude.forEach(file => {
    if (fs.existsSync(file)) {
      archive.file(file, { name: file });
    }
  });

  archive.finalize();
});

app.get('/api/project-info', (req, res) => {
  res.json({
    appName: 'News India',
    packageName: 'com.news.twa',
    websiteUrl: 'https://news-alpha-two.vercel.app/',
    version: '1.0.0',
    minSdk: 24,
    targetSdk: 34
  });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`News TWA Builder running at http://0.0.0.0:${PORT}`);
});
