import {DeliverMethod} from "../../Order.types.ts";
import {Radio, RadioGroup, Text} from "@fluentui/react-components";

interface DeliveryMethodsProps {
    deliverMethods: DeliverMethod[];
    selectedDeliverId: string | null;
    onChange: (id: string) => void;
    label: string;
}

export function DeliveryMethods({ deliverMethods, selectedDeliverId, onChange, label }: DeliveryMethodsProps) {
    return (
        <div>
            <Text>{label}</Text>
            <RadioGroup
                value={selectedDeliverId || ''}
                onChange={(e) => onChange((e.target as HTMLInputElement).value)}
            >
                {deliverMethods.map(method => (
                    <Radio key={method.id} value={method.id} label={`${method.name} (${method.price} PLN)`} />
                ))}
            </RadioGroup>
        </div>
    );
}