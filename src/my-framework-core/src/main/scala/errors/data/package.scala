package errors
import cats.data.Validated
package object data {

  type ValidatedErr[Data] = Validated[String, Data]
}
