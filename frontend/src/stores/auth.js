import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/utils/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const modulesStr = localStorage.getItem('modules')
  const modules = ref(modulesStr ? JSON.parse(modulesStr) : [])
  
  // 如果存在 token 和 user，但 modules 为空数组，说明数据不完整，清除认证信息
  if (token.value && user.value && (!modulesStr || modules.value.length === 0)) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('modules')
    token.value = ''
    user.value = null
    modules.value = []
  }

  const isAuthenticated = ref(!!token.value)

  const login = async (username, password) => {
    try {
      const response = await api.post('/auth/login', { username, password })
      // 后端返回的结构是：{ userId, username, name, token, role, modules }
      token.value = response.token
      user.value = {
        id: response.userId,
        username: response.username,
        name: response.name,
        role: response.role
      }
      modules.value = response.modules || []
      localStorage.setItem('token', response.token)
      localStorage.setItem('user', JSON.stringify(user.value))
      localStorage.setItem('modules', JSON.stringify(modules.value))
      isAuthenticated.value = true
      return response
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    token.value = ''
    user.value = null
    modules.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('modules')
    isAuthenticated.value = false
  }

  return {
    token,
    user,
    modules,
    isAuthenticated,
    login,
    logout
  }
})

