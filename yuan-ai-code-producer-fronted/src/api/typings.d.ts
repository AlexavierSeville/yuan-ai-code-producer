declare namespace API {
  type App = {
    /** 主键 ID */
    id?: number
    /** 应用名称 */
    appName?: string
    /** 应用封面 URL */
    cover?: string
    /** 初始化 Prompt */
    initPrompt?: string
    /** 代码生成类型（枚举） */
    codeGenType?: string
    /** 部署标识，用于预览与访问 */
    deployKey?: string
    /** 最近部署时间 */
    deployedTime?: string
    /** 优先级（用于精选排序等） */
    priority?: number
    /** 创建者用户 ID */
    userId?: number
    /** 编辑时间 */
    editTime?: string
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
    /** 逻辑删除标记：0-未删，1-已删 */
    isDelete?: number
  }

  type AppAddRequest = {
    /** 应用的初始化 Prompt，用于指导代码生成 */
    initPrompt: string
  }

  type AppAdminUpdateRequest = {
    /** 应用 ID */
    id: number
    /** 应用名称 */
    appName?: string
    /** 应用封面 URL */
    cover?: string
    /** 应用优先级，用于精选排序等场景 */
    priority?: number
  }

  type AppDeployRequest = {
    /** 应用 ID */
    appId: number
  }

  type AppQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    /** 应用 ID */
    id?: number
    /** 应用名称（支持模糊匹配） */
    appName?: string
    /** 应用封面 URL */
    cover?: string
    /** 初始化 Prompt（支持模糊匹配） */
    initPrompt?: string
    /** 代码生成类型（枚举值） */
    codeGenType?: string
    /** 部署标识，用于静态资源访问 */
    deployKey?: string
    /** 优先级（数值越大越靠前） */
    priority?: number
    /** 创建者用户 ID */
    userId?: number
  }

  type AppUpdateRequest = {
    /** 应用 ID */
    id: number
    /** 应用名称 */
    appName?: string
  }

  type AppVO = {
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number
    createTime?: string
    updateTime?: string
    user?: UserVO
  }

  type BaseResponseAppVO = {
    code?: number
    data?: AppVO
    message?: string
  }

  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseLoginUserVO = {
    code?: number
    data?: LoginUserVO
    message?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageAppVO = {
    code?: number
    data?: PageAppVO
    message?: string
  }

  type BaseResponsePageChatHistory = {
    code?: number
    data?: PageChatHistory
    message?: string
  }

  type BaseResponsePageUserVO = {
    code?: number
    data?: PageUserVO
    message?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUser = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO = {
    code?: number
    data?: UserVO
    message?: string
  }

  type ChatHistory = {
    id?: number
    message?: string
    messageType?: string
    appId?: number
    userId?: number
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type ChatHistoryQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    message?: string
    messageType?: string
    appId?: number
    userId?: number
    lastCreateTime?: string
  }

  type chatToGenCodeParams = {
    /** 应用 ID */
    appId: number
    /** 用户输入的需求/提示词 */
    message: string
  }

  type DeleteRequest = {
    id?: number
  }

  type getAppVOByIdByAdminParams = {
    /** 应用 ID */
    id: number
  }

  type getAppVOByIdParams = {
    /** 应用 ID */
    id: number
  }

  type getUserByIdParams = {
    /** 用户 ID */
    id: number
  }

  type getUserVOByIdParams = {
    /** 用户 ID */
    id: number
  }

  type listAppChatHistoryParams = {
    appId: number
    pageSize?: number
    lastCreateTime?: string
  }

  type LoginUserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
    updateTime?: string
  }

  type PageAppVO = {
    records?: AppVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageChatHistory = {
    records?: ChatHistory[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageUserVO = {
    records?: UserVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type ServerSentEventString = true

  type serveStaticResourceParams = {
    /** 部署唯一标识 */
    deployKey: string
  }

  type User = {
    /** 主键 ID */
    id?: number
    /** 账号（唯一） */
    userAccount?: string
    /** 密码（加密存储） */
    userPassword?: string
    /** 用户昵称 */
    userName?: string
    /** 用户头像 URL */
    userAvatar?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色：user/admin */
    userRole?: string
    /** 编辑时间 */
    editTime?: string
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
    /** 逻辑删除标记：0-未删，1-已删 */
    isDelete?: number
  }

  type UserAddRequest = {
    /** 用户昵称 */
    userName?: string
    /** 账号（唯一） */
    userAccount?: string
    /** 用户头像 URL */
    userAvatar?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色：user/admin */
    userRole?: string
  }

  type UserLoginRequest = {
    /** 账号 */
    userAccount: string
    /** 密码 */
    userPassword: string
  }

  type UserQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    /** 用户 ID */
    id?: number
    /** 用户昵称（支持模糊匹配） */
    userName?: string
    /** 账号（支持模糊匹配） */
    userAccount?: string
    /** 用户简介 */
    userProfile?: string
    /** 用户角色：user/admin/ban */
    userRole?: string
  }

  type UserRegisterRequest = {
    /** 账号 */
    userAccount: string
    /** 密码 */
    userPassword: string
    /** 确认密码 */
    checkPassword: string
  }

  type UserUpdateRequest = {
    id?: number
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
  }
}
