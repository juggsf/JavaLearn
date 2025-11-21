<template>
  <div class="login-container">
    <div v-if="isLoggedIn && currentUser" class="profile-container">
      <h1 class="login-title">Welcome, {{ currentUser.username }}!</h1>
      <div class="user-info">
        <p><strong>Email:</strong> {{ currentUser.email }}</p>
        <p><strong>Username:</strong> {{ currentUser.username }}</p>
      </div>
      <button @click="logout" class="logout-button">Logout</button>
    </div>
    
    <div v-else class="login-form">
      <h1 class="login-title">User Login</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label" for="username">Username:</label>
          <input
            id="username"
            v-model="loginForm.username"
            type="text"
            class="form-input"
            placeholder="Enter username"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label" for="password">Password:</label>
          <input
            id="password"
            v-model="loginForm.password"
            type="password"
            class="form-input"
            placeholder="Enter password"
            required
          />
        </div>
        <button type="submit" class="login-button" :disabled="loading">
          {{ loading ? "Logging in..." : "Login" }}
        </button>
      </form>
      
      <div class="batch-test-section" style="margin-top: 2rem; padding: 1.5rem; border: 2px dashed #667eea; border-radius: 8px;">
        <h3 style="margin-bottom: 1rem; color: #667eea;">ThreadLocal & Token Test</h3>
        <p style="font-size: 0.9rem; color: #666; margin-bottom: 1rem;">Simulate login requests from 100 different users (username: 1-100, password: same as username)</p>
        <button @click="startBatchLogin" class="batch-button" :disabled="batchTesting">
          {{ batchTesting ? `Testing Users ${currentBatchIndex}/100...` : "Start Batch Login Test" }}
        </button>
        
        <div v-if="serverStats" class="server-stats" style="margin-top: 1rem; background: #e3f2fd; padding: 1rem; border-radius: 5px;">
          <h4 style="margin-bottom: 0.5rem; color: #1976d2;">Server Stats:</h4>
          <p style="margin: 0.25rem 0; font-size: 0.9rem;">Total Users: {{ serverStats.totalUsers }}</p>
          <p style="margin: 0.25rem 0; font-size: 0.9rem;">ThreadLocal User: {{ serverStats.threadLocalUser || 'None' }}</p>
          <p style="margin: 0.25rem 0; font-size: 0.9rem;">ThreadLocal Token: {{ serverStats.threadLocalToken }}</p>
        </div>
        
        <div v-if="batchResults.length > 0" class="batch-results" style="margin-top: 1rem;">
          <h4>Test Results:</h4>
          <div class="results-stats" style="background: #f8f9fa; padding: 1rem; border-radius: 5px; margin-top: 0.5rem;">
            <p><strong>Total Users Tested:</strong> {{ batchResults.length }}</p>
            <p><strong>Successful Logins:</strong> <span style="color: #28a745;">{{ successfulLogins }}</span></p>
            <p><strong>Failed Logins:</strong> <span style="color: #dc3545;">{{ failedLogins }}</span></p>
            <p><strong>Success Rate:</strong> {{ successRate.toFixed(1) }}%</p>
            <p><strong>Average Response Time:</strong> {{ averageResponseTime.toFixed(0) }}ms</p>
          </div>
          
          <div class="results-list" style="max-height: 200px; overflow-y: auto; margin-top: 1rem; border: 1px solid #ddd; border-radius: 5px;">
            <div v-for="result in batchResults.slice(-20)" :key="result.username" 
                 :style="{ padding: '0.5rem', borderBottom: '1px solid #eee', color: result.success ? '#28a745' : '#dc3545' }">
              <small>User {{ result.username }}: {{ result.success ? 'SUCCESS' : 'FAILED' }} 
                <span v-if="result.responseTime">({{ result.responseTime }}ms)</span>
                <span v-if="!result.success">- {{ result.message }}</span>
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
    
    <div v-if="!isLoggedIn" style="margin-top: 2rem; padding: 1rem; background: #f8f9fa; border-radius: 5px; font-size: 0.9rem; color: #666;">
      <p><strong>Test Accounts:</strong></p>
      <p>Username: admin, Password: admin123</p>
      <p>Username: user, Password: user123</p>
      <p>Batch Test: Users 1-100 (password = username)</p>
    </div>
  </div>
</template>

<script>
import axios from "axios"

export default {
  name: "App",
  data() {
    return {
      loginForm: {
        username: "",
        password: ""
      },
      loading: false,
      errorMessage: "",
      successMessage: "",
      isLoggedIn: false,
      currentUser: null,
      token: null,
      batchTesting: false,
      batchResults: [],
      currentBatchIndex: 0,
      serverStats: null
    }
  },
  computed: {
    successfulLogins() {
      return this.batchResults.filter(r => r.success).length
    },
    failedLogins() {
      return this.batchResults.filter(r => !r.success).length
    },
    successRate() {
      if (this.batchResults.length === 0) return 0
      return (this.successfulLogins / this.batchResults.length) * 100
    },
    averageResponseTime() {
      const successfulResults = this.batchResults.filter(r => r.success && r.responseTime)
      if (successfulResults.length === 0) return 0
      const totalTime = successfulResults.reduce((sum, r) => sum + r.responseTime, 0)
      return totalTime / successfulResults.length
    }
  },
  created() {
    this.checkAuthStatus()
  },
  methods: {
    async handleLogin() {
      this.loading = true
      this.errorMessage = ""
      this.successMessage = ""
      
      try {
        const response = await axios.post("http://localhost:8080/api/auth/login", {
          username: this.loginForm.username,
          password: this.loginForm.password
        })
        
        if (response.data.success) {
          this.token = response.data.token
          this.currentUser = response.data.user
          this.isLoggedIn = true
          
          // Store token and user info in localStorage
          localStorage.setItem('token', this.token)
          localStorage.setItem('user', JSON.stringify(this.currentUser))
          
          // Set default authorization header for future requests
          axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
          
          this.successMessage = "Login successful!"
        }
      } catch (error) {
        if (error.response && error.response.data) {
          this.errorMessage = error.response.data.message
        } else {
          this.errorMessage = "Login failed, please check network connection"
        }
      } finally {
        this.loading = false
      }
    },
    logout() {
      this.isLoggedIn = false
      this.currentUser = null
      this.token = null
      
      // Remove token and user info from localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      
      // Remove authorization header
      delete axios.defaults.headers.common['Authorization']
      
      // Reset form
      this.loginForm = {
        username: "",
        password: ""
      }
      
      this.successMessage = "Logged out successfully"
    },
    checkAuthStatus() {
      const savedToken = localStorage.getItem('token')
      const savedUser = localStorage.getItem('user')
      
      if (savedToken && savedUser) {
        this.token = savedToken
        this.currentUser = JSON.parse(savedUser)
        this.isLoggedIn = true
        
        // Set default authorization header
        axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`
        
        // Verify token with server
        this.verifyToken()
      }
      
      // Fetch server stats
      this.fetchServerStats()
    },
    async verifyToken() {
      try {
        const response = await axios.get('http://localhost:8080/api/auth/profile', {
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
        
        if (response.data.success) {
          // Token is valid, update user info
          this.currentUser = response.data.user
          localStorage.setItem('user', JSON.stringify(this.currentUser))
        }
      } catch (error) {
        // Token is invalid, logout
        this.logout()
      }
    },
    async fetchServerStats() {
      try {
        const response = await axios.get('http://localhost:8080/api/auth/stats')
        this.serverStats = response.data
      } catch (error) {
        console.error('Failed to fetch server stats:', error)
      }
    },
    async startBatchLogin() {
      this.batchTesting = true
      this.batchResults = []
      this.currentBatchIndex = 0
      this.errorMessage = ""
      this.successMessage = ""
      
      // Test login for users 1-100 sequentially
      for (let i = 1; i <= 100; i++) {
        this.currentBatchIndex = i
        await this.testUserLogin(i.toString(), i.toString())
        
        // Add small delay between requests
        await new Promise(resolve => setTimeout(resolve, 50))
        
        // Update server stats every 10 users
        if (i % 10 === 0) {
          await this.fetchServerStats()
        }
      }
      
      // Final stats update
      await this.fetchServerStats()
      
      this.batchTesting = false
      this.successMessage = `Batch test completed! Success rate: ${this.successRate.toFixed(1)}%`
    },
    async testUserLogin(username, password) {
      try {
        const startTime = Date.now()
        const response = await axios.post("http://localhost:8080/api/auth/login", {
          username: username,
          password: password
        }, {
          headers: {
            // Don't use default auth header for batch testing
            'Authorization': "Bearer " + username
          }
        })
        const endTime = Date.now()
        
        if (response.data.success) {
          this.batchResults.push({
            username: username,
            success: true,
            message: `Token received (${response.data.token.substring(0, 20)}...)`,
            responseTime: endTime - startTime
          })
        }
      } catch (error) {
        this.batchResults.push({
          username: username,
          success: false,
          message: error.response?.data?.message || 'Network error',
          responseTime: null
        })
      }
    }
  }
}
</script>

<style scoped>
.profile-container {
  text-align: center;
}

.user-info {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  margin: 2rem 0;
  text-align: left;
}

.logout-button {
  background: #dc3545;
  color: white;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.logout-button:hover {
  background: #c82333;
}

.batch-button {
  width: 100%;
  padding: 0.75rem;
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: transform 0.2s;
}

.batch-button:hover:not(:disabled) {
  transform: translateY(-2px);
}

.batch-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.batch-results h4 {
  margin: 1rem 0 0.5rem 0;
  color: #333;
}

.results-stats p {
  margin: 0.25rem 0;
}
</style>
