import {DeliverMethod} from "../../Order.types.ts";
import {Radio, RadioGroup, Text} from "@fluentui/react-components";
import {useDeliveryMethodsStyles} from "./DeliveryMethod.styled.tsx";

interface DeliveryMethodsProps {
    deliverMethods: DeliverMethod[];
    selectedDeliverId: string | null;
    onChange: (id: string) => void;
    label: string;
}

export function DeliveryMethods({deliverMethods, selectedDeliverId, onChange, label}: DeliveryMethodsProps) {
    const styles = useDeliveryMethodsStyles();

    return (
        <div className={styles.container}>
            <Text className={styles.label}>{label}</Text>
            <RadioGroup
                name="deliveryMethod"
                value={selectedDeliverId || ''}
                onChange={(_, data) => {
                    console.log('Selected delivery:', data.value);
                    console.log('delivery methods: ', deliverMethods);
                    onChange(data.value)
                }}
            >
                {deliverMethods.map(method => {
                    return (
                        <Radio
                            key={method.uuid}
                            value={String(method.uuid)}
                            label={`${method.name} (${method.price} PLN)`}
                        />
                    );
                })}
            </RadioGroup>
        </div>
    );
}