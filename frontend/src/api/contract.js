import api from '@/utils/api'

/**
 * 合同管理 API
 */

/**
 * 获取合同列表
 * @param {Object} params - 查询参数
 * @param {string} [params.keyword] - 搜索关键词（合同编号、合同名称、客户名称）
 * @returns {Promise<Array>}
 */
export const getContractList = (params) => {
  return api.get('/contracts', { params })
}

/**
 * 根据 ID 获取合同详情
 * @param {string} id - 合同 UUID
 * @returns {Promise<Object>}
 */
export const getContractById = (id) => {
  return api.get(`/contracts/${id}`)
}

/**
 * 新增合同
 * @param {Object} data - 合同数据
 * @returns {Promise<Object>}
 */
export const createContract = (data) => {
  return api.post('/contracts', data)
}

/**
 * 修改合同
 * @param {string} id - 合同 UUID
 * @param {Object} data - 合同数据
 * @returns {Promise<Object>}
 */
export const updateContract = (id, data) => {
  return api.put(`/contracts/${id}`, data)
}

/**
 * 删除合同
 * @param {string} id - 合同 UUID
 * @returns {Promise<void>}
 */
export const deleteContract = (id) => {
  return api.delete(`/contracts/${id}`)
}

/**
 * 保存合同（不提交审批，草稿状态）
 * @param {Object} data - 合同数据
 * @returns {Promise<Object>}
 */
export const saveContractDraft = (data) => {
  return api.post('/contracts/save', data)
}

/**
 * 提交合同审批
 * @param {string} id - 合同 UUID
 * @param {Object} config - 审批节点配置
 * @returns {Promise<Object>}
 */
export const submitContractApproval = (id, config) => {
  return api.post(`/contracts/${id}/submit`, config)
}

/**
 * 查看流程审批状态
 * @param {string} id - 合同 UUID
 * @returns {Promise<Array>}
 */
export const getWorkflowStatus = (id) => {
  return api.get(`/contracts/${id}/workflow-status`)
}

/**
 * 查询合同执行进度
 * @param {string} id - 合同 UUID
 * @returns {Promise<Object>}
 */
export const getExecutionProgress = (id) => {
  return api.get(`/contracts/${id}/execution-progress`)
}

/**
 * 更新合同执行进度
 * @param {string} id - 合同 UUID
 * @param {Object} data - 进度数据
 * @returns {Promise<Object>}
 */
export const updateExecutionProgress = (id, data) => {
  return api.put(`/contracts/${id}/execution-progress`, data)
}

/**
 * 查询所有合同的执行进度列表
 * @returns {Promise<Array>}
 */
export const getAllExecutionProgress = () => {
  return api.get('/contracts/execution-progress/list')
}







