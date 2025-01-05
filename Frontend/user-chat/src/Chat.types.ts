export interface ChatResponse {
    receiverId: string;
    lastMessage: string;
    lastMessageTime: string;
    read: boolean;
    unreadCount: number;
    email?: string;
    username?: string;
}

export interface MessageResponse {
    uuid: string;
    content: string;
    senderId: string;
    receiverId: string;
    dateAdded: string;
    read: boolean;
}

export interface UserDetailsResponse {
    userId: string;
    username?: string;
    email?: string;
}

export interface MessagesResponse {
    messages: MessageResponse[];
    sender: UserDetailsResponse;
    receiver: UserDetailsResponse;
}
