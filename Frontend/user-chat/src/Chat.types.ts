export interface MessageResponse {
    uuid: string;
    content: string;
    senderId: string;
    receiverId: string;
    dateAdded: string;
    isRead: boolean;
}

export interface SendMessageRequest {
    content: string;
    receiverId: string;
}

export interface ChatResponse {
    receiverId: string;
    username: string;
    lastMessage: string;
    lastMessageTime: string;
    isRead: boolean;
    unreadCount: number;
}

export interface MessagesResponse {
    messages: MessageResponse[];
    sender: UserDetailsResponse;
    receiver: UserDetailsResponse;
}

export interface UserDetailsResponse {
    userId: string;
    username: string;
    email: string;
}

export interface UserDetailsResponse {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    addresses: AddressResponse[];
    imageUrl: string;
}

export interface AddressResponse {
    uuid: string;
    street: string;
    city: string;
    state: string;
    country: string;
    postalCode: string;
}