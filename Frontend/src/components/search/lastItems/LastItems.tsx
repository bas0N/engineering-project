import { LastItemsItem, LastItemsItemDescription, LastItemsWrapper, LastItemsItemImage, LastItemsItemHeader } from "./LastItems.styled";
import {Tooltip} from '@fluentui/react-components';

export type ItemType = {
    name: string;
    image: string;
    id: string;
}

interface LastItemsProps {
    items: ItemType[];
}
export const LastItems = ({items}: LastItemsProps) => {

    return (<LastItemsWrapper>
            {items.map((elem, ind) => (
                <Tooltip content={elem.name}>
                    <LastItemsItem  
                        href={`/products/${elem.id}`}
                        key={`${elem.name}-${ind}`}
                        customBorderRadius={ind === 0 ? '15px 0px 0px 15px' : ind === items.length - 1 ? '0px 15px 15px 0px': 'none'}
                    >
                        <LastItemsItemImage src={elem.image} alt={elem.name} loading='lazy' />
                        <LastItemsItemDescription> 
                            <LastItemsItemHeader>
                                {elem.name.length > 40 ? elem.name.substring(0,37)+'...' : elem.name}
                            </LastItemsItemHeader>
                        </LastItemsItemDescription>
                    </LastItemsItem>
                </Tooltip>))}
        </LastItemsWrapper>
    );
};