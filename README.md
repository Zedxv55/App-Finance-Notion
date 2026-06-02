# 💰 CLAUDE.md — Finance Tracker PWA
> คำสั่งสร้างแอพรายรับ-รายจ่าย สำหรับคนขี้เกียจ  
> ฟรีทุกอย่าง | Notion + Google Sheets + Firebase + LocalStorage | PWA

---

## 🎯 Project Overview

สร้าง Progressive Web App (PWA) สำหรับจัดการรายรับ-รายจ่าย ที่:
- **ง่ายมาก** — กด 2 ปุ่ม เสร็จ
- **ฟรีทุกอย่าง** — ไม่มีค่าใช้จ่ายใดๆ
- **ข้อมูลปลอดภัย** — sync หลายที่พร้อมกัน
- **AI ช่วยวางแผน** — มี fallback 3 API

---

## 🏗️ Tech Stack (ฟรีทั้งหมด)

| Layer | Technology | Free Tier |
|-------|-----------|-----------|
| Frontend | React + Vite (PWA) | ✅ ฟรี |
| Hosting | Vercel / Netlify / GitHub Pages | ✅ ฟรี |
| Auth | Notion OAuth | ✅ ฟรี |
| DB หลัก | Notion API | ✅ ฟรี (1000 blocks/integration) |
| DB สำรอง 1 | Google Sheets API | ✅ ฟรี |
| DB สำรอง 2 | Firebase Firestore | ✅ ฟรี (1GB / 50k reads/day) |
| DB ออฟไลน์ | LocalStorage + IndexedDB | ✅ ฟรีในเครื่อง |
| AI หลัก | Google AI Studio (Gemini) API Key 1 | ✅ ฟรี |
| AI สำรอง 1 | Google AI Studio API Key 2 | ✅ ฟรี |
| AI สำรอง 2 | Google AI Studio API Key 3 | ✅ ฟรี |

---

## 📁 โครงสร้างโปรเจกต์

```
finance-tracker/
├── public/
│   ├── manifest.json          # PWA manifest
│   ├── icons/                 # App icons (192x192, 512x512)
│   └── sw.js                  # Service Worker
├── src/
│   ├── main.jsx
│   ├── App.jsx
│   ├── config/
│   │   ├── notion.js          # Notion config + database IDs
│   │   ├── firebase.js        # Firebase config
│   │   ├── googleSheets.js    # Google Sheets config
│   │   └── aiKeys.js          # AI API keys (3 ตัว + fallback logic)
│   ├── services/
│   │   ├── storage/
│   │   │   ├── notionService.js
│   │   │   ├── firebaseService.js
│   │   │   ├── sheetsService.js
│   │   │   ├── localService.js
│   │   │   └── syncManager.js   # จัดการ sync ทุก storage
│   │   ├── ai/
│   │   │   ├── geminiService.js  # AI fallback chain
│   │   │   └── aiPrompts.js     # Prompt templates
│   │   └── importExport/
│   │       ├── excelImport.js   # นำเข้าจาก Excel
│   │       └── csvExport.js     # Export CSV
│   ├── pages/
│   │   ├── LoginPage.jsx        # หน้า Login + Notion OAuth
│   │   ├── ConnectPage.jsx      # เชื่อมต่อ Notion DB
│   │   ├── DashboardPage.jsx    # หน้าแรก - สรุปยอด
│   │   ├── AddTransactionPage.jsx  # หน้าใส่ยอด (รายรับ/รายจ่าย)
│   │   ├── AccountsPage.jsx     # หน้าบัญชี
│   │   ├── AIAdvisorPage.jsx    # หน้า AI วางแผน
│   │   └── CalendarPage.jsx     # หน้าปฏิทิน
│   ├── components/
│   │   ├── layout/
│   │   │   ├── BottomNav.jsx    # แถบ 5 เมนูด้านล่าง
│   │   │   └── TopBar.jsx
│   │   ├── dashboard/
│   │   │   ├── BalanceSummary.jsx
│   │   │   ├── RecentTransactions.jsx
│   │   │   └── QuickStats.jsx
│   │   ├── transaction/
│   │   │   ├── TransactionTypeSelector.jsx  # ปุ่ม รายรับ / รายจ่าย
│   │   │   └── TransactionForm.jsx
│   │   ├── accounts/
│   │   │   └── AccountCard.jsx
│   │   ├── ai/
│   │   │   └── AIChatBubble.jsx
│   │   └── calendar/
│   │       └── FinanceCalendar.jsx
│   ├── hooks/
│   │   ├── useSync.js           # hook จัดการ sync
│   │   ├── useAI.js             # hook AI fallback
│   │   └── useAuth.js           # Notion OAuth
│   └── styles/
│       ├── global.css
│       └── theme.css            # CSS variables light/dark/auto
├── .env.example                 # template ค่า environment variables
├── .env.local                   # ค่าจริง (ห้าม commit!)
├── package.json
└── vite.config.js
```

---

## 🔐 Environment Variables (.env.local)

```env
# ===== NOTION =====
VITE_NOTION_CLIENT_ID=your_notion_client_id
VITE_NOTION_CLIENT_SECRET=your_notion_client_secret
VITE_NOTION_REDIRECT_URI=http://localhost:5173/auth/callback
VITE_NOTION_DB_URL=https://app.notion.com/p/Finance-Tracker-2952d0cf63fd80ca8048e1500246ce97

# ===== FIREBASE (Firestore) =====
VITE_FIREBASE_API_KEY=
VITE_FIREBASE_AUTH_DOMAIN=
VITE_FIREBASE_PROJECT_ID=
VITE_FIREBASE_STORAGE_BUCKET=
VITE_FIREBASE_MESSAGING_SENDER_ID=
VITE_FIREBASE_APP_ID=

# ===== GOOGLE SHEETS =====
VITE_GOOGLE_CLIENT_ID=
VITE_GOOGLE_API_KEY=
VITE_SPREADSHEET_ID=         # Google Sheet ID สำหรับ backup

# ===== GOOGLE AI STUDIO (3 API Keys + Fallback) =====
VITE_GEMINI_API_KEY_1=       # Key หลัก
VITE_GEMINI_API_KEY_2=       # Fallback ถ้า Key 1 หมด
VITE_GEMINI_API_KEY_3=       # Fallback ถ้า Key 2 หมด
VITE_GEMINI_MODEL=gemini-2.0-flash-exp
```

---

## 📱 หน้าแอพ (5 เมนู)

### หน้า 0: Login Page
```
เป้าหมาย: Login ผ่าน Notion OAuth
```

**Flow:**
1. กด "เข้าสู่ระบบด้วย Notion"
2. Redirect ไป Notion OAuth
3. รับ access_token กลับมา
4. บันทึก token ลง LocalStorage
5. Redirect ไปหน้า Connect Database

**Component:**
```jsx
// src/pages/LoginPage.jsx
const LoginPage = () => {
  const handleNotionLogin = () => {
    const notionAuthUrl = `https://api.notion.com/v1/oauth/authorize?client_id=${NOTION_CLIENT_ID}&response_type=code&owner=user&redirect_uri=${encodeURIComponent(REDIRECT_URI)}`
    window.location.href = notionAuthUrl
  }
  return (
    <div className="login-screen">
      <Logo />
      <h1>Finance Tracker</h1>
      <p>จัดการเงินง่ายๆ สำหรับคนขี้เกียจ</p>
      <button onClick={handleNotionLogin} className="btn-notion">
        <NotionIcon /> เข้าสู่ระบบด้วย Notion
      </button>
    </div>
  )
}
```

---

### หน้า 0.5: Connect Database Page
```
เป้าหมาย: เชื่อมต่อ Notion Database ที่มีอยู่แล้ว
Link: https://app.notion.com/p/Finance-Tracker-2952d0cf63fd80ca8048e1500246ce97
```

**Flow:**
1. แสดงรายการ Notion databases ที่ user มีสิทธิ์
2. หรือ paste URL ของ database โดยตรง
3. ตรวจสอบ schema ว่ามี columns ที่ต้องการ
4. เชื่อมต่อสำเร็จ → ไปหน้า Dashboard

---

### หน้า 1: Dashboard 🏠
```
เป้าหมาย: สรุปภาพรวมการเงินทั้งหมด
```

**แสดงผล:**
- **ยอดเงินรวมทุกบัญชี** (ตัวเลขใหญ่ตรงกลาง)
- **รายรับเดือนนี้** (สีเขียว)
- **รายจ่ายเดือนนี้** (สีแดง)
- **ยอดคงเหลือเดือนนี้** = รายรับ - รายจ่าย
- **รายการล่าสุด** 5 รายการ
- **Quick Add Button** (ปุ่มลัด + ลอยอยู่มุมขวาล่าง)

**Data Source:** ดึงจาก Notion → Firebase → LocalStorage (ตามลำดับ)

---

### หน้า 2: Add Transaction ➕
```
เป้าหมาย: บันทึกรายรับหรือรายจ่ายใหม่
```

**Step 1 — เลือกประเภท:**
```
┌─────────────────────────────┐
│                             │
│   [💚 รายรับ]  [❤️ รายจ่าย]  │
│                             │
│   (ปุ่มใหญ่ 2 ปุ่ม โง่ๆ)    │
└─────────────────────────────┘
```

**Step 2 — กรอกข้อมูล (รายรับ):**

| Field | Type | Required | Note |
|-------|------|----------|------|
| วันที่ | Date Picker | ✅ | default = วันนี้ |
| รายการ | Text Input | ✅ | ชื่อรายการ |
| จำนวน (บาท) | Number Input | ✅ | ตัวเลขเท่านั้น |
| เข้าบัญชีไหน | Dropdown | ✅ | ดึงจากหน้าบัญชี |
| แหล่งที่มา | Dropdown/Text | ✅ | เงินเดือน, ค้าขาย ฯลฯ |
| หมายเหตุ | Textarea | ❌ | optional |

**Step 2 — กรอกข้อมูล (รายจ่าย):**

| Field | Type | Required | Note |
|-------|------|----------|------|
| วันที่ | Date Picker | ✅ | default = วันนี้ |
| รายการ | Text Input | ✅ | ชื่อรายการ |
| จำนวน (บาท) | Number Input | ✅ | ระบบจะ -ยอด อัตโนมัติ |
| จ่ายจากบัญชีไหน | Dropdown | ✅ | ดึงจากหน้าบัญชี |
| หมวดหมู่ | Dropdown | ✅ | อาหาร, เดินทาง, ฯลฯ |
| หมายเหตุ | Textarea | ❌ | optional |

**Submit Flow:**
```
กดส่ง → บันทึก LocalStorage ทันที (ตอบสนองทันที)
      → background sync ไป Notion API
      → background sync ไป Firebase  
      → background sync ไป Google Sheets
      → แสดง ✅ สำเร็จ (หรือ error พร้อม retry)
```

---

### หน้า 3: Accounts 🏦
```
เป้าหมาย: ดูยอดเงินแต่ละบัญชีและยอดรวม
```

**แสดงผล:**
- Card แต่ละบัญชีพร้อมยอดปัจจุบัน
- ยอดรวมทุกบัญชีด้านบน
- ประวัติรายการของแต่ละบัญชี (กดดูได้)
- เพิ่ม/แก้ไข/ลบบัญชีได้

**บัญชีตัวอย่างที่ควรมี:**
- ธนาคาร (เช่น กสิกร, SCB, ฯลฯ)
- เงินสด
- Wallet (PromptPay, TrueMoney, ฯลฯ)
- + เพิ่มบัญชีเอง

---

### หน้า 4: AI Advisor 🤖
```
เป้าหมาย: AI วิเคราะห์การเงินและวางแผนให้
```

**Features:**
- Chat กับ AI เกี่ยวกับการเงินของตัวเอง
- AI ดึงข้อมูล transaction ล่าสุดมาวิเคราะห์
- วางแผนการออม, แนะนำลดรายจ่าย
- สรุปการใช้จ่ายเป็นภาษาพูดง่ายๆ

**AI Fallback Chain (3 Keys):**
```javascript
// src/services/ai/geminiService.js

const AI_KEYS = [
  process.env.VITE_GEMINI_API_KEY_1,
  process.env.VITE_GEMINI_API_KEY_2,
  process.env.VITE_GEMINI_API_KEY_3,
]

export async function askAI(prompt, context) {
  for (let i = 0; i < AI_KEYS.length; i++) {
    try {
      const res = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=${AI_KEYS[i]}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            contents: [{
              parts: [{ text: buildPrompt(prompt, context) }]
            }]
          })
        }
      )
      if (res.ok) {
        const data = await res.json()
        return data.candidates[0].content.parts[0].text
      }
      // ถ้า rate limit หรือ error → ลอง key ถัดไป
      if (res.status === 429 || res.status === 503) continue
      throw new Error(`AI Error: ${res.status}`)
    } catch (err) {
      if (i === AI_KEYS.length - 1) throw new Error('AI ทุก key ใช้ไม่ได้ชั่วคราว')
      continue
    }
  }
}

function buildPrompt(userMessage, financialContext) {
  return `คุณคือที่ปรึกษาการเงินส่วนตัวภาษาไทย วิเคราะห์ข้อมูลการเงินด้านล่างแล้วตอบ

ข้อมูลการเงินปัจจุบัน:
- รายรับเดือนนี้: ${financialContext.income} บาท
- รายจ่ายเดือนนี้: ${financialContext.expense} บาท
- ยอดคงเหลือ: ${financialContext.balance} บาท
- รายจ่ายหมวดสูงสุด: ${financialContext.topExpenseCategory}
- รายการล่าสุด: ${JSON.stringify(financialContext.recentTransactions)}

คำถามของผู้ใช้: ${userMessage}

ตอบเป็นภาษาไทยที่เข้าใจง่าย กระชับ ให้คำแนะนำจริงๆ`
}
```

---

### หน้า 5: Calendar 📅
```
เป้าหมาย: ดูรายรับรายจ่ายแบบ Calendar View
```

**Features:**
- Calendar แสดงแต่ละวัน
- สีเขียว = มีรายรับวันนั้น
- สีแดง = มีรายจ่ายวันนั้น
- สีเหลือง = มีทั้งสองอย่าง
- กดวันดูรายการของวันนั้น
- ดู Monthly summary ด้านล่าง

---

## 🔄 Storage Sync Architecture

```
┌─────────────────────────────────────────────────────┐
│                  syncManager.js                     │
│                                                     │
│  1. บันทึก LocalStorage ทันที (ออฟไลน์รองรับ)      │
│  2. Sync ไป Notion (หลัก) — background             │
│  3. Sync ไป Firebase (สำรอง) — background          │
│  4. Sync ไป Google Sheets (backup) — background    │
│                                                     │
│  ถ้า offline → queue งานไว้ → sync เมื่อ online    │
└─────────────────────────────────────────────────────┘
```

```javascript
// src/services/storage/syncManager.js

export class SyncManager {
  async save(transaction) {
    // 1. บันทึก local ทันที
    localService.save(transaction)
    
    // 2. sync แบบ parallel ทุกที่
    Promise.allSettled([
      notionService.addRow(transaction),
      firebaseService.addDoc(transaction),
      sheetsService.appendRow(transaction),
    ]).then(results => {
      results.forEach((r, i) => {
        if (r.status === 'rejected') {
          console.warn(`Sync failed for storage ${i}:`, r.reason)
          this.queue.push({ storage: i, data: transaction }) // retry later
        }
      })
    })
  }
  
  async read() {
    // ลอง Notion ก่อน → Firebase → LocalStorage
    try { return await notionService.getAll() } catch {}
    try { return await firebaseService.getAll() } catch {}
    return localService.getAll() // fallback ออฟไลน์เสมอ
  }
}
```

---

## 📊 Notion Database Schema

Notion database ที่ URL นี้ต้องมี columns เหล่านี้:

### Table: Transactions
| Column Name | Notion Type | Note |
|-------------|------------|------|
| Name | Title | ชื่อรายการ |
| Type | Select | "รายรับ" / "รายจ่าย" |
| Amount | Number | จำนวนเงิน (บวกเสมอ) |
| Date | Date | วันที่ |
| Account | Relation | → Accounts table |
| Category | Select | หมวดหมู่ |
| Source | Text | แหล่งที่มา / ปลายทาง |
| Note | Text | หมายเหตุ (optional) |
| CreatedAt | Created time | auto |

### Table: Accounts
| Column Name | Notion Type | Note |
|-------------|------------|------|
| Name | Title | ชื่อบัญชี |
| Type | Select | ธนาคาร, เงินสด, Wallet |
| InitialBalance | Number | ยอดเริ่มต้น |
| Color | Text | สีของ card (#hex) |

---

## 📥 Import / Export

### Import จาก Excel/CSV
```javascript
// src/services/importExport/excelImport.js

// ใช้ library: xlsx (SheetJS) — ฟรี
// npm install xlsx

import * as XLSX from 'xlsx'

export function importFromExcel(file) {
  return new Promise((resolve) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const workbook = XLSX.read(e.target.result, { type: 'binary' })
      const sheet = workbook.Sheets[workbook.SheetNames[0]]
      const rows = XLSX.utils.sheet_to_json(sheet)
      
      // แปลง rows เป็น transaction format
      const transactions = rows.map(row => ({
        name: row['รายการ'] || row['Name'] || row['Description'],
        type: row['ประเภท'] || row['Type'],
        amount: parseFloat(row['จำนวน'] || row['Amount'] || 0),
        date: row['วันที่'] || row['Date'],
        account: row['บัญชี'] || row['Account'],
        category: row['หมวดหมู่'] || row['Category'],
        note: row['หมายเหตุ'] || row['Note'] || '',
      }))
      
      resolve(transactions)
    }
    reader.readAsBinaryString(file)
  })
}
```

### Export ไป Notion จาก Excel (กรณีไม่มีการเชื่อมต่อ)
```
1. user upload ไฟล์ Excel
2. app parse ข้อมูล
3. preview ก่อน 10 rows แรก
4. กด "นำเข้า Notion" → เพิ่มทีละ batch (5 rows/sec เพื่อไม่ให้ rate limit)
5. แสดง progress bar
```

---

## 📲 PWA Setup

### vite.config.js
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['favicon.ico', 'apple-touch-icon.png'],
      manifest: {
        name: 'Finance Tracker',
        short_name: 'Finance',
        description: 'รายรับรายจ่ายสำหรับคนขี้เกียจ',
        theme_color: '#000000',
        background_color: '#ffffff',
        display: 'standalone',
        orientation: 'portrait',
        icons: [
          { src: 'icons/icon-192.png', sizes: '192x192', type: 'image/png' },
          { src: 'icons/icon-512.png', sizes: '512x512', type: 'image/png' },
          { src: 'icons/icon-512.png', sizes: '512x512', type: 'image/png', purpose: 'any maskable' }
        ]
      },
      workbox: {
        globPatterns: ['**/*.{js,css,html,ico,png,svg}'],
        runtimeCaching: [
          {
            urlPattern: /^https:\/\/api\.notion\.com\/.*/i,
            handler: 'NetworkFirst',
            options: { cacheName: 'notion-api-cache', expiration: { maxEntries: 50 } }
          }
        ]
      }
    })
  ]
})
```

---

## 🎨 Theme System (Auto Light/Dark)

```css
/* src/styles/theme.css */
:root {
  color-scheme: light dark;
}

/* Light Mode */
@media (prefers-color-scheme: light) {
  :root {
    --bg-primary: #ffffff;
    --bg-secondary: #f5f5f5;
    --bg-card: #ffffff;
    --text-primary: #1a1a1a;
    --text-secondary: #666666;
    --color-income: #16a34a;       /* เขียวเข้ม */
    --color-expense: #dc2626;      /* แดงเข้ม */
    --color-balance: #2563eb;      /* น้ำเงิน */
    --accent: #6366f1;
    --border: rgba(0,0,0,0.1);
  }
}

/* Dark Mode */
@media (prefers-color-scheme: dark) {
  :root {
    --bg-primary: #0f0f0f;
    --bg-secondary: #1a1a1a;
    --bg-card: #1e1e1e;
    --text-primary: #f5f5f5;
    --text-secondary: #999999;
    --color-income: #4ade80;       /* เขียวสว่าง */
    --color-expense: #f87171;      /* แดงสว่าง */
    --color-balance: #60a5fa;      /* ฟ้าสว่าง */
    --accent: #818cf8;
    --border: rgba(255,255,255,0.1);
  }
}
```

---

## 📦 Dependencies

```json
{
  "dependencies": {
    "react": "^18.3.0",
    "react-dom": "^18.3.0",
    "react-router-dom": "^6.26.0",
    "@notionhq/client": "^2.2.15",
    "firebase": "^10.13.0",
    "xlsx": "^0.18.5",
    "react-calendar": "^5.0.0",
    "date-fns": "^3.6.0"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.3.0",
    "vite": "^5.4.0",
    "vite-plugin-pwa": "^0.20.5"
  }
}
```

---

## 🚀 Getting Started

```bash
# 1. สร้างโปรเจกต์
npm create vite@latest finance-tracker -- --template react
cd finance-tracker
npm install

# 2. ติดตั้ง dependencies
npm install @notionhq/client firebase xlsx react-router-dom react-calendar date-fns
npm install -D vite-plugin-pwa

# 3. copy .env.example เป็น .env.local แล้วใส่ค่า
cp .env.example .env.local

# 4. รัน dev server
npm run dev

# 5. Build for production
npm run build

# 6. Deploy ไป Vercel (ฟรี)
npx vercel --prod
```

---

## 🔑 วิธีขอ API Keys (ฟรีทั้งหมด)

### Notion API
1. ไป https://www.notion.so/my-integrations
2. "New Integration" → ตั้งชื่อ → เลือก workspace
3. Copy **Internal Integration Token**
4. สร้าง Notion OAuth app ที่ https://developers.notion.com

### Google AI Studio (3 Keys)
1. ไป https://aistudio.google.com/apikey
2. "Create API Key" × 3 (ใช้ email เดียวกันได้)
3. แต่ละ key มี Free tier: 1,500 requests/day

### Firebase
1. ไป https://console.firebase.google.com
2. สร้าง Project ใหม่ → Enable Firestore
3. ตั้ง Rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### Google Sheets API
1. ไป https://console.cloud.google.com
2. Enable "Google Sheets API"
3. สร้าง OAuth 2.0 Client ID
4. สร้าง Google Sheet → copy Spreadsheet ID จาก URL

---

## ✅ Checklist การพัฒนา

### Phase 1 — Core (ทำก่อน)
- [ ] ตั้งค่า Vite + React + PWA
- [ ] Login ด้วย Notion OAuth
- [ ] เชื่อมต่อ Notion Database
- [ ] หน้า Dashboard พื้นฐาน
- [ ] หน้าใส่ยอด (รายรับ/รายจ่าย)
- [ ] บันทึกลง Notion

### Phase 2 — Storage Sync
- [ ] LocalStorage offline support
- [ ] Firebase sync
- [ ] Google Sheets backup
- [ ] SyncManager + retry queue

### Phase 3 — Features
- [ ] หน้าบัญชีพร้อมยอดรวม
- [ ] AI Advisor (Gemini + fallback chain)
- [ ] Calendar View
- [ ] Import Excel
- [ ] Export CSV

### Phase 4 — Polish
- [ ] Dark/Light auto theme
- [ ] Loading states + skeleton
- [ ] Error handling ทุกจุด
- [ ] Offline indicator
- [ ] Install PWA prompt

---

## 💡 Tips สำหรับ AI (เวลาสั่ง Claude)

เวลาสั่งให้ Claude สร้างแต่ละส่วน ให้บอกว่า:

```
"ดู CLAUDE.md แล้วสร้าง [ชื่อ component] โดยใช้ tech stack ตามที่กำหนด"

ตัวอย่าง:
- "สร้าง LoginPage.jsx ตาม CLAUDE.md"
- "สร้าง syncManager.js ที่ sync ไป Notion, Firebase, Sheets พร้อมกัน"
- "สร้าง TransactionForm.jsx สำหรับรายรับ"
- "สร้าง AI fallback chain ใน geminiService.js"
```

---

*อัปเดตล่าสุด: 2025 | สร้างสำหรับคนขี้เกียจที่อยากรู้เรื่องเงิน 💰*
