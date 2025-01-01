import { DeliverMethod } from "../../Order.types";
import { Radio, RadioGroup } from "@fluentui/react-components";
import { DeliveryMethodsContainer, DeliveryMethodsLabel } from "./DeliveryMethod.styled";

interface DeliveryMethodsProps {
    deliverMethods: DeliverMethod[];
    selectedDeliverId: string | null;
    onChange: (id: string) => void;
    label: string;
}

export const DeliveryMethods = ({
    deliverMethods, 
    selectedDeliverId, 
    onChange, 
    label
}: DeliveryMethodsProps) => (
    <DeliveryMethodsContainer>
        <DeliveryMethodsLabel>{label}</DeliveryMethodsLabel>
        <RadioGroup
            name="deliveryMethod"
            value={selectedDeliverId || ''}
            onChange={(_, data) => {
                onChange(data.value)
            }}
        >
            {deliverMethods.map(method => (
                    <Radio
                        key={method.uuid}
                        value={method.uuid}
                        label={`${method.name} (${method.price} PLN)`}
                    />
                )
            )}
        </RadioGroup>
    </DeliveryMethodsContainer>
);