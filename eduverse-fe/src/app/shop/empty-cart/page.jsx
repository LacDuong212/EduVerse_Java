import PageMetaData from '@/components/PageMetaData';
import EmptyCart from './components/EmptyCart';
const EmptyCartPage = () => {
  return <>
      <PageMetaData title="Empty Cart" />
      <main>
        <EmptyCart />
      </main>
    </>;
};
export default EmptyCartPage;
