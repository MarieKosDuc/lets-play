export interface User {
    id: string,
    name: string,
    profilePicture: string,
    roles?: string[],
    refreshToken?: string
}