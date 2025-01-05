
import { ChangeEvent, useState } from "react";
import { useTranslation } from "react-i18next";
import { 
    Button, 
    Dropdown,
    DropdownProps, 
    Option,
    Input,
    Text,
    Textarea, 
} from "@fluentui/react-components";
import { 
    AddProductHeader, 
    AddProductWrapper, 
    AddProductInput 
} from "./AddProduct.styled";
import { Categories } from "./categories/Categories";
import { Features } from "./features/Features";
import { Details } from './details/Details';

export const AddProduct = () => {
    const {t} = useTranslation();

    const [title, setTitle] = useState('');
    const [categories, setCategories] = useState<string[]>([]);
    const [mainCategory, setMainCategory] = useState('');
    const [features, setFeatures] = useState<string[]>([]);
    const [description, setDescription] = useState('');
    const [details, setDetails] = useState<Record<string,string>>({});
    const [price, setPrice] = useState(0);
    const [store, setStore] = useState('');
    const [finalizingProduct, setFinalizingProduct] = useState(false);

    const handleAddingProduct = () => {

    };

    const onOptionSelect: DropdownProps['onOptionSelect'] = (_ev, data) => {
        setMainCategory(data.optionText ?? "");
    };

    return <AddProductWrapper>
        {
            finalizingProduct ? (<>
                <Input 
                    value={store}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setStore(e.currentTarget.value)}
                    placeholder={t('addProduct.storePlaceholder')} 
                />
                <Input 
                    value={String(price)}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setPrice(Number(e.currentTarget.value))}
                    type='number' 
                    min='0' 
                    placeholder={t('addProduct.pricePlaceholder')} 
                />
                <Dropdown
                    value={mainCategory}
                    selectedOptions={[mainCategory]}
                    onOptionSelect={onOptionSelect}
                >
                    {categories.map((category) => (<Option 
                        value={category} 
                        text={category} 
                        key={`main-category-${category}`}>
                            <Text>{category}</Text>
                    </Option>))}
                </Dropdown>
                <Button onClick={() => setFinalizingProduct(false)}>
                    {t('addProduct.goBack')}
                </Button>
                <Button onClick={() => handleAddingProduct()}>
                    {t('addProduct.finalizeProductCreation')}
                </Button>
            </>) : (<>
                <AddProductHeader as='h2'>
                    {t('addProduct.title')}
                </AddProductHeader>
                <AddProductInput 
                    value={title}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setTitle(e.currentTarget.value)}
                    appearance='underline'
                    placeholder={t('addProduct.titlePlaceholder')} 
                />
                <Textarea 
                    placeholder={t('addProduct.descriptionPlaceholder')}
                    value={description}
                    onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setDescription(e.currentTarget.value)}
                />
                <Categories 
                    categories={categories} 
                    setCategories={setCategories} 
                />
                <Features 
                    features={features}
                    setFeatures={setFeatures}
                />
                <Details
                    details={details}
                    setDetails={setDetails}
                />
                <Button onClick={() => setFinalizingProduct(true)}>
                    {t('addProduct.finalizeCreation')}
                </Button>
            </>)
        }
    </AddProductWrapper>
};