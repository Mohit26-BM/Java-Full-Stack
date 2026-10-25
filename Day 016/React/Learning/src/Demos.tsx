import ProductComponent from './ProductComponent'

import ClickEvent from './events/ClickEvent'
import InlineAndArgs from './events/InlineAndArgs'
import ClickCounter from './events/ClickCounter'
import ToggleMessage from './events/ToggleMessage'
import TextInput from './events/TextInput'
import TextArea from './events/TextArea'
import SelectDropdown from './events/SelectDropdown'
import Checkbox from './events/Checkbox'
import RadioButtons from './events/RadioButtons'
import FormSubmit from './events/FormSubmit'
import MouseEvents from './events/MouseEvents'
import FocusBlur from './events/FocusBlur'
import KeyboardEvents from './events/KeyboardEvents'
import DoubleClick from './events/DoubleClick'
import Propagation from './events/Propagation'
import PreventDefault from './events/PreventDefault'

import SimpleList from './lists/SimpleList'
import ObjectList from './lists/ObjectList'
import FilterList from './lists/FilterList'
import SortList from './lists/SortList'
import SearchList from './lists/SearchList'
import AddItem from './lists/AddItem'
import RemoveItem from './lists/RemoveItem'
import UpdateItem from './lists/UpdateItem'
import UseState from './hooks/UseState'
import UseEffect from './hooks/UseEffect'
import UseContext from './hooks/UseContext'
import UseRef from './hooks/UseRef'
import UseReducer from './hooks/UseReducer'
import UseMemo from './hooks/UseMemo'
import UseCallback from './hooks/UseCallback'
import UseLayoutEffect from './hooks/UseLayoutEffect'
import UseImperativeHandle from './hooks/UseImperativeHandle'
import UseId from './hooks/UseId'
import UseTransition from './hooks/UseTransition'
import UseDeferredValue from './hooks/UseDeferredValue'
import UseSyncExternalStore from './hooks/UseSyncExternalStore'
import UseDebugValue from './hooks/UseDebugValue'
import UseInsertionEffect from './hooks/UseInsertionEffect'
import UseActionState from './hooks/UseActionState'
import UseOptimistic from './hooks/UseOptimistic'
import UseApi from './hooks/UseApi'
import CustomHook from './hooks/CustomHook'

function Demos() {
  return (
    <>
      <h1>Events</h1>
      <h3>Click</h3><ClickEvent />
      <h3>Inline & arguments</h3><InlineAndArgs />
      <h3>Counter</h3><ClickCounter />
      <h3>Toggle</h3><ToggleMessage />
      <h3>Text input</h3><TextInput />
      <h3>Textarea</h3><TextArea />
      <h3>Select</h3><SelectDropdown />
      <h3>Checkbox</h3><Checkbox />
      <h3>Radio</h3><RadioButtons />
      <h3>Form submit</h3><FormSubmit />
      <h3>Mouse</h3><MouseEvents />
      <h3>Focus / blur</h3><FocusBlur />
      <h3>Keyboard</h3><KeyboardEvents />
      <h3>Double click</h3><DoubleClick />
      <h3>Propagation</h3><Propagation />
      <h3>preventDefault</h3><PreventDefault />

      <h1>Lists</h1>
      <h3>Simple list</h3><SimpleList />
      <h3>Object list</h3><ObjectList />
      <h3>Filter</h3><FilterList />
      <h3>Sort</h3><SortList />
      <h3>Search</h3><SearchList />
      <h3>Add</h3><AddItem />
      <h3>Remove</h3><RemoveItem />
      <h3>Update</h3><UpdateItem />

      <h1>Hooks</h1>
      <h3>useState</h3><UseState />
      <h3>useEffect</h3><UseEffect />
      <h3>useContext</h3><UseContext />
      <h3>useRef</h3><UseRef />
      <h3>useReducer</h3><UseReducer />
      <h3>useMemo</h3><UseMemo />
      <h3>useCallback</h3><UseCallback />
      <h3>useLayoutEffect</h3><UseLayoutEffect />
      <h3>useImperativeHandle</h3><UseImperativeHandle />
      <h3>useId</h3><UseId />
      <h3>useTransition</h3><UseTransition />
      <h3>useDeferredValue</h3><UseDeferredValue />
      <h3>useSyncExternalStore</h3><UseSyncExternalStore />
      <h3>useDebugValue</h3><UseDebugValue />
      <h3>useInsertionEffect</h3><UseInsertionEffect />
      <h3>useActionState</h3><UseActionState />
      <h3>useOptimistic</h3><UseOptimistic />
      <h3>use (Promise)</h3><UseApi />
      <h3>Custom hook</h3><CustomHook />

      <h1>Products</h1>
      <ProductComponent />
    </>
  )
}

export default Demos
