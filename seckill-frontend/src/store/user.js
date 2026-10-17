import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userId: localStorage.getItem('userId') || '',
    nickname: localStorage.getItem('nickname') || '',
    role: localStorage.getItem('role') || ''
  }),
  actions: {
    setLogin(data) {
      this.token = data.token
      this.userId = data.userId
      this.nickname = data.nickname
      this.role = data.role
      localStorage.setItem('token', data.token)
      localStorage.setItem('userId', data.userId)
      localStorage.setItem('nickname', data.nickname)
      localStorage.setItem('role', data.role)
    },
    logout() {
      this.token = ''
      this.userId = ''
      this.nickname = ''
      this.role = ''
      localStorage.clear()
    }
  }
})