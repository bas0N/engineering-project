import {Client, IMessage, StompConfig, StompSubscription} from '@stomp/stompjs';
import {ChatResponse, SendMessageRequest} from "../Chat.types.ts";
import axios from "axios";

interface ChatServiceOptions {
    token: string;
    onMessage: (msg: any) => void;
    onConnect?: () => void;
    onDisconnect?: () => void;
}

export class ChatService {
    private client: Client
    private subscription: StompSubscription | null = null;

    constructor(private options: ChatServiceOptions) {
        const stompConfig: StompConfig = {
            webSocketFactory: () => new WebSocket(`${import.meta.env.VITE_CHAT_URL}?token=${this.options.token}`),
            debug: str => console.log('STOMP:', str),
            onConnect: () => {
                console.log('Connected via STOMP');
                this.subscribeToMessages();
                if (options.onConnect) {
                    options.onConnect();
                }
            },
            onStompError: frame => {
                console.error('Broker error: ' + frame.headers['message']);
                console.error('Broker error details: ' + frame.body);
            },
            onWebSocketClose: () => {
                console.warn('WebSocket closed');
                if (options.onDisconnect) {
                    options.onDisconnect();
                }
            },
            reconnectDelay: 5000,
        };
        this.client = new Client(stompConfig);
    }

    public activate() {
        console.log('Activating STOMP...');
        this.client.activate();
    }

    private subscribeToMessages() {
        console.log('Subscribing to /user/queue/messages...');
        this.subscription = this.client.subscribe('/user/queue/messages', (message: IMessage) => {
            if (message.body) {
                const parsed = JSON.parse(message.body);
                this.options.onMessage(parsed);
            }
        });
    }

    public sendMessage(content: string, receiverId: string) {
        const msg: SendMessageRequest = {
            content,
            receiverId,
        }
        this.client.publish({
            destination: '/app/sendMessage',
            body: JSON.stringify(msg),
            headers: {
                Authorization: this.options.token ? `Bearer ${this.options.token}` : '',
            }
        });
    }

    public deactivate() {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
        this.client.deactivate();
    }

    static async fetchChats(token: string): Promise<ChatResponse[]> {
        const response = await axios.get<ChatResponse[]>(
            `${import.meta.env.VITE_API_URL}message/chats`,
            {
                headers: {Authorization: `Bearer ${token}`}
            }
        );
        return response.data as ChatResponse[];
    }

    static async fetchUnreadCount(token: string): Promise<number> {
        const response = await axios.get<number>(
            `${import.meta.env.VITE_API_URL}message/unreadCount`,
            {headers: {Authorization: `Bearer ${token}`}}
        );
        return response.data as number;
    }

}